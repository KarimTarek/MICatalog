package eg.com.vodafone.mi.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import eg.com.vodafone.mi.connection.ConnectionManager;
import eg.com.vodafone.mi.connection.Environment;
import eg.com.vodafone.mi.export.ProductsExporter;
import eg.com.vodafone.mi.sql.SQLStatement;
import eg.com.vodafone.mi.utility.TablesUtilityViewer;
import eg.com.vodafone.mi.utility.VaadinUtil;

@SuppressWarnings("serial")
public class ExportProductsView extends CustomComponent implements View
{
    private ProgressBar progressBar;
    private VerticalLayout resultLayout;
    private VerticalLayout content;
    
    private String propertiesFile;
    private String title;

    public ExportProductsView(String propertiesFile, String title)
    {
	this.setCompositionRoot(new VerticalLayout());
	this.propertiesFile = propertiesFile;
	this.title = title;
    }

    @Override
    public void enter(ViewChangeEvent event)
    {
	Panel panel = new Panel(this.title);
	panel.setSizeFull();

	this.setCompositionRoot(panel);
	this.setSizeFull();

	content = new VerticalLayout();
	content.setMargin(true);
	panel.setContent(content);
	content.setWidth("100%");
	content.setHeight("");

	FormLayout form = new FormLayout();
	final ComboBox cmboSourceEnv = new ComboBox("Source Environment: ");
	VaadinUtil.adjustComboBox(cmboSourceEnv, "Select the Source Environment ", getEnvironments());
	form.addComponent(cmboSourceEnv);

	final CheckBox chkValidateDestEnv = new CheckBox("Filter against destination environment?");
	chkValidateDestEnv.setValue(false);
	form.addComponent(chkValidateDestEnv);

	final ComboBox cmboDestEnv = new ComboBox("Destination Environment: ");
	cmboDestEnv.setEnabled(false);
	VaadinUtil.adjustComboBox(cmboDestEnv, "Select the Destination Environment ", getEnvironments());
	form.addComponent(cmboDestEnv);

	chkValidateDestEnv.addValueChangeListener(new ValueChangeListener()
	{

	    @Override
	    public void valueChange(ValueChangeEvent event)
	    {
		cmboDestEnv.setEnabled((Boolean) event.getProperty().getValue());
	    }
	});

	final TextField txtPRDID = new TextField("Product ID:");
	txtPRDID.setSizeFull();
	txtPRDID.setRequired(true);
//	txtPRDID.addValidator(new RegexpValidator("[0-9]+(,[0-9]+)*", "Invalid Input Format"));
	form.addComponent(txtPRDID);

	Label lblHint = new Label("Comma separated list of Products IDs (e.g. 1,2,3)");
	lblHint.setStyleName(Runo.LABEL_SMALL);
	form.addComponent(lblHint);

	content.addComponent(form);

	Button btnExport = new Button("Export");
	btnExport.setClickShortcut(KeyCode.ENTER);
	btnExport.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		if (VaadinUtil.isFieldsValid(cmboSourceEnv, txtPRDID))
		{
		    if (chkValidateDestEnv.getValue())
		    {
			if (!VaadinUtil.isFieldsValid(cmboDestEnv))
			{
			    resultLayout.removeAllComponents();
			    return;
			}
		    }

		    progressBar = new ProgressBar();
		    progressBar.setIndeterminate(true);

		    resultLayout.removeAllComponents();
		    resultLayout.addComponent(progressBar);
		    resultLayout.setComponentAlignment(progressBar, Alignment.BOTTOM_CENTER);

		    Environment destEnv;

		    if (cmboDestEnv.getValue() == null)
			destEnv = null;
		    else
			destEnv = Environment.fromString(cmboDestEnv.getValue().toString());

		    WorkerThread worker = new WorkerThread(Environment.fromString(cmboSourceEnv.getValue().toString()),
			    destEnv, txtPRDID.getValue(), chkValidateDestEnv.getValue());
		    worker.start();
		}
		else
		{
		    resultLayout.removeAllComponents();
		}
	    }
	});

	content.addComponent(btnExport);
	content.setComponentAlignment(btnExport, Alignment.BOTTOM_CENTER);

	resultLayout = new VerticalLayout();
	resultLayout.setSizeFull();
	content.addComponent(resultLayout);
	content.setComponentAlignment(resultLayout, Alignment.BOTTOM_CENTER);
    }

    private Container getEnvironments()
    {
	List<String> envs = new ArrayList<String>();
	//envs.add(Environment.TEST_35.getText());
	envs.add(Environment.TEST_65.getText());
	//envs.add(Environment.TEST_71.getText());
	envs.add(Environment.PRD.getText());
	//envs.add(Environment.DEV.getText());
	IndexedContainer container = new IndexedContainer((List<String>) envs);
	return container;
    }

    private class WorkerThread extends Thread
    {
	private Environment srcEnv;
	private Environment destEnv;
	private String prdIDs;
	private boolean validateDestEnv;

	public WorkerThread(Environment srcEnv, Environment destEnv, String prdIDs, Boolean validateDestEnv)
	{
	    super();
	    this.srcEnv = srcEnv;
	    this.destEnv = destEnv;
	    this.prdIDs = prdIDs;
	    this.validateDestEnv = validateDestEnv;
	}

	@Override
	public void run()
	{
	    ProductsExporter exporter = new ProductsExporter(propertiesFile,
		    ConnectionManager.getDataSource(srcEnv), ConnectionManager.getDataSource(destEnv), validateDestEnv);

	    String[] prdsIDs;

	    if (prdIDs.contains(","))
		prdsIDs = prdIDs.split(",");
	    else
		prdsIDs = new String[]
		{ prdIDs };

	    final List<SQLStatement> sqls = exporter.export(prdsIDs);

	    UI.getCurrent().access(new Runnable()
	    {
		@Override
		public void run()
		{
		    resultLayout.removeAllComponents();
		    resultLayout.addComponent(createLayout(sqls));
		    UI.getCurrent().push();
		}
	    });
	}

	private Component createLayout(List<SQLStatement> sqls)
	{
	    VerticalLayout layout = new VerticalLayout();

	    Label lblHeader = new Label("Review the Configuration");
	    lblHeader.addStyleName(Runo.LABEL_H1);
	    layout.addComponent(lblHeader);

	    Label lblTxt = new Label("Review the product(s) configuration below which will be exported.");
	    layout.addComponent(lblTxt);
	    layout.setSizeFull();

	    HorizontalLayout horizontalLayout = new HorizontalLayout();
	    Button btnDownload = new Button("Download Script");

	    horizontalLayout.addComponent(btnDownload);

	    layout.addComponent(horizontalLayout);
	    layout.setComponentAlignment(horizontalLayout, Alignment.TOP_RIGHT);

	    StreamResource myResource = TablesUtilityViewer.createResource("MigrationScript", sort(sqls));
	    FileDownloader fileDownloader = new FileDownloader(myResource);
	    fileDownloader.extend(btnDownload);

	    layout.addComponent(TablesUtilityViewer.createTablesLayout(sqls));

	    return layout;
	}

	private List<SQLStatement> sort(List<SQLStatement> sqls)
	{
	    List<SQLStatement> sorted = new ArrayList<SQLStatement>(sqls);
	    
	    Collections.sort(sorted, new Comparator<SQLStatement>()
	    {

		@Override
		public int compare(SQLStatement arg0, SQLStatement arg1)
		{
		    return arg0.getTable().compareTo(arg1.getTable());
		}
	    });
	    
	    return sorted;
	}
    }
}
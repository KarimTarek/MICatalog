package eg.com.vodafone.mi.view.wizard;

import java.util.List;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.themes.Runo;

import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.sql.SQLProductGenerator;
import eg.com.vodafone.mi.sql.SQLStatement;
import eg.com.vodafone.mi.utility.TablesUtilityViewer;

@SuppressWarnings("serial")
public class ReviewChangesView extends AbstractStepView
{
    public ReviewChangesView(Product product)
    {
	super(product);
	
	this.setCompositionRoot(new VerticalLayout());
    }

    private void createLayout()
    {
	VerticalLayout layout = new VerticalLayout();

	Label lblHeader = new Label("Review Changes");
	lblHeader.addStyleName(Runo.LABEL_H1);
	layout.addComponent(lblHeader);

	Label lblTxt = new Label(
		"Review the product configuration below. In case you want to change the below configuration, just go back and update product.");
	layout.addComponent(lblTxt);
	layout.setSizeFull();
	layout.setMargin(true);

	HorizontalLayout horizontalLayout = new HorizontalLayout();
	Button btnDownload = new Button("Download Script");

	horizontalLayout.addComponent(btnDownload);

	layout.addComponent(horizontalLayout);
	layout.setComponentAlignment(horizontalLayout, Alignment.TOP_RIGHT);

	Product product = getProduct();

	SQLProductGenerator sqlGenerator = new SQLProductGenerator(product);
	List<SQLStatement> list = sqlGenerator.generate();

	StreamResource myResource = TablesUtilityViewer.createResource("Product-" + product.getPrdID(), list);
	FileDownloader fileDownloader = new FileDownloader(myResource);
	fileDownloader.extend(btnDownload);

	layout.addComponent(TablesUtilityViewer.createTablesLayout(list));

	this.setCompositionRoot(layout);
    }

    @Override
    public Component getContent()
    {
	createLayout();
	return this;
    }

    @Override
    public boolean onBack()
    {
	return true;
    }

    @Override
    protected void processStep()
    {
    }

    @Override
    public String getCaption()
    {
	return "Review Changes";
    }
}

package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts.MainLayout;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@Route(value = "help", layout = MainLayout.class)
@PageTitle("Help")
public class HelpView extends VerticalLayout {

    public HelpView() {
        setAlignItems(CENTER);
        addClassName("help");

        addTitle();

        addRules();

        addImages();

        addLinks();
    }

    private void addLinks() {
        VerticalLayout linkList = new VerticalLayout();
        linkList.add(new H5("Links"));
        linkList.add(new Anchor("https://www.youtube.com/watch?v=oLcpiQYtwYg", new ListItem("Tutorial (english)")));
        linkList.add(new Anchor("https://www.youtube.com/watch?v=PMCsU79CI30", new ListItem("Tutorial (german)")));
        linkList.setAlignItems(CENTER);
        add(linkList);
    }

    private void addImages() {
        VerticalLayout imageLayout = new VerticalLayout();
        imageLayout.setAlignItems(CENTER);
        imageLayout.addClassName("image-layout");
        StreamResource helpStartImageResource = new StreamResource("help_start.jpg",
                () -> getClass().getResourceAsStream("/images/help_start.jpg"));
        Image helpStartImage = new Image(helpStartImageResource, "Help Start");
        helpStartImage.setMaxWidth(25, Unit.EM);
        imageLayout.add(helpStartImage);

        Icon icon = new Icon("arrow-down");
        imageLayout.add(icon);

        StreamResource helpFinishImageResource = new StreamResource("help_finished.jpg",
                () -> getClass().getResourceAsStream("/images/help_finished.jpg"));
        Image helpFinishedImage = new Image(helpFinishImageResource, "Help Finished");
        helpFinishedImage.setMaxWidth(25, Unit.EM);
        imageLayout.add(helpFinishedImage);
        add(imageLayout);
    }

    private void addRules() {
        add(new UnorderedList(
                new ListItem("Rule 1: the sum of the numbers in two adjacent blocks must equal the number in the block above the two adjacent blocks "),
                new ListItem("Rule 2: the pyramid is solved when all blocks are filled and rule 1 holds for all adjacent blocks ")
        ));
    }

    private void addTitle() {
        H3 title = new H3("How to play Math-Pyramid?");
        add(title);
    }
}

package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Theme(value = Lumo.class)
// Global styles apply only to elements which are not located in shadow dom
@CssImport("./styles/shared-styles.css")
@Push
@UIScope
public class MainLayout extends AppLayout {

    private final Logger logger = LoggerFactory.getLogger(MainLayout.class);
    private final UserService userService;

    @Autowired
    public MainLayout(UserService userService, Header header) {
        this.userService = userService;
        logger.debug("Creating MainLayout {} for user {}", this, userService.getUserName());
        addToNavbar(header);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        logger.debug("Detaching MainLayout {} for user {}", this, userService.getUserName());
        super.onDetach(detachEvent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        logger.debug("Attaching MainLayout {} for user {}", this, userService.getUserName());
        super.onAttach(attachEvent);
    }
}

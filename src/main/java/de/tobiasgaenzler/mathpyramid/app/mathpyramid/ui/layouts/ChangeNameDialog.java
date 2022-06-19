package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.layouts;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ChangeNameDialog extends Dialog {

    private final Logger logger = LoggerFactory.getLogger(ChangeNameDialog.class);

    public ChangeNameDialog(UserService userService, Consumer<String> nameChangedCallback) {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        VerticalLayout changeNameForm = new VerticalLayout();
        changeNameForm.setId("change-name-form");
        TextField userNameField = createUserNameField();
        changeNameForm.add(userNameField);

        Button saveButton = createSaveButton(userService, nameChangedCallback, userNameField);
        changeNameForm.add(saveButton);
        add(changeNameForm);
    }

    private Button createSaveButton(UserService userService,
                                    Consumer<String> nameChangedCallback,
                                    TextField userNameField) {
        Button saveButton = new Button("Save");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(e -> {
            if (!userNameField.isEmpty()) {
                logger.info("Changing user name from {} to {}", userService.getUserName(), userNameField.getValue());
                userService.setUserName(userNameField.getValue());
                nameChangedCallback.accept(userService.getUserName());
            }
            close();
        });
        return saveButton;
    }

    private TextField createUserNameField() {
        TextField userNameField = new TextField();
        userNameField.addClassName("user-name");
        userNameField.setPlaceholder("Enter your name");
        userNameField.setClearButtonVisible(true);
        userNameField.setAutofocus(true);
        userNameField.setPrefixComponent(VaadinIcon.USER.create());
        return userNameField;
    }
}

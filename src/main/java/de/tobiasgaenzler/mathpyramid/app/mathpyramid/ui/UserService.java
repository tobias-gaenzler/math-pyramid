package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui;

import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@UIScope
public class UserService {
    private String userName;

    public String getUserName() {
        if (userName == null) {
            userName = UUID.randomUUID().toString().substring(0, 7);
        }
        return userName;
    }
}

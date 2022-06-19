package de.tobiasgaenzler.mathpyramid.app.mathpyramid.ui.services;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@VaadinSessionScope
public class UserService {
    private final List<String> NAMES = List.of("Olivia", "Amelia", "Isla", "Ava", "Mia", "Isabella", "Grace", "Sophia", "Lily", "Emily", "Freya", "Ivy", "Ella", "Charlotte", "Poppy", "Florence", "Evie", "Rosie", "Willow", "Phoebe", "Sophie", "Evelyn", "Sienna", "Elsie", "Sofia", "Alice", "Ruby", "Matilda", "Isabelle", "Harper", "Daisy", "Emilia", "Jessica", "Maya", "Eva", "Luna", "Eliza", "Millie", "Chloe", "Penelope", "Maisie", "Esme", "Aria", "Scarlett", "Imogen", "Thea", "Harriet", "Ada", "Layla", "Mila", "Eleanor", "Violet", "Elizabeth", "Bella", "Rose", "Emma", "Erin", "Molly", "Lola", "Lucy", "Hallie", "Bonnie", "Ellie", "Zara", "Maria", "Robyn", "Hannah", "Nancy", "Arabella", "Holly", "Aurora", "Jasmine", "Lottie", "Orla", "Delilah", "Gracie", "Georgia", "Darcie", "Iris", "Amber", "Amelie", "Anna", "Maryam", "Abigail", "Lilly", "Annabelle", "Ayla", "Sara", "Beatrice", "Edith", "Clara", "Heidi", "Margot", "Summer", "Zoe", "Martha", "Lara", "Mabel", "Lyla", "Felicity", "Oliver", "George", "Arthur", "Noah", "Harry", "Muhammad", "Leo", "Jack", "Oscar", "Charlie", "Henry", "Jacob", "Thomas", "Freddie", "Alfie", "Theodore", "Theo", "William", "Archie", "Joshua", "Alexander", "James", "Isaac", "Edward", "Lucas", "Tommy", "Max", "Finley", "Mohammed", "Ethan", "Logan", "Benjamin", "Teddy", "Joseph", "Sebastian", "Arlo", "Adam", "Harrison", "Elijah", "Daniel", "Samuel", "Louie", "Albie", "Mason", "Reuben", "Rory", "Hugo", "Jaxon", "Luca", "Zachary", "Reggie", "Louis", "Albert", "Hunter", "Dylan", "David", "Jude", "Frankie", "Ezra", "Roman", "Toby", "Riley", "Ronnie", "Frederick", "Carter", "Stanley", "Gabriel", "Bobby", "Jesse", "Michael", "Mohammad", "Grayson", "Elliot", "Liam", "Jenson", "Harvey", "Ralph", "Jayden", "Jake", "Harley", "Ellis", "Charles", "Elliott", "Jasper", "Felix", "Finn", "Rowan", "Caleb", "Leon", "Chester", "Ibrahim", "Ollie", "Jackson", "Alfred", "Hudson", "Ryan", "Matthew", "Rupert", "Luke", "Nathan");

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private String userName;

    public String getUserName() {
        if (userName == null) {
            userName = NAMES.get(new Random().nextInt(NAMES.size()));
            logger.info("Assigning new random username {}", userName);
        }
        return userName;
    }

    public void setUserName(String userName) {
        logger.info("Assigning new  username {}", userName);
        this.userName = userName;
    }
}

package com.example.guessmaster;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guessmaster.R;

public class GuessMaster extends AppCompatActivity { //Android UI replaces old command-line codes
    private TextView entityName; //Entity Name
    private TextView ticketsum; //Count for Tickets
    private Button guessButton; //Guess Button submitted
    private EditText userIn; //Input field for date section
    private Button btnClearContent;
    private String answer;
    private ImageView entityImage; //Shows the image for the game

    private int numOfEntities;
    private Entity[] entities;
    private int[] tickets;
    private int numOfTickets;
    private String entName;
    private int entityid = 0;
    private int currentTicketWon = 0;
    private int totalTicketNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.xml.activity_guess_master);

        // Initialize views
        entityName = findViewById(R.id.entityName);
        ticketsum = findViewById(R.id.ticket);
        guessButton = findViewById(R.id.btnGuess);
        userIn = findViewById(R.id.guessinput);
        btnClearContent = findViewById(R.id.btnClear);
        entityImage = findViewById(R.id.entityImage);

        // Initialize game entities
        initializeEntities();

        // Set up button listeners
        btnClearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEntity();
            }
        });

        guessButton.setOnClickListener(new View.OnClickListener() { //Button clicked
            @Override
            public void onClick(View v) {
                playGame(entities[entityid]);
            }
        });

        // Start the game with a random entity
        changeEntity();
    }

    private void initializeEntities() {
        numOfEntities = 0;
        entities = new Entity[10];
        tickets = new int[100];
        numOfTickets = 0;

        // Create entities
        Country usa = new Country(
                "United States",
                new Date("July", 4, 1776),
                "Washington DC",
                0.1);

        Person myCreator = new Person(
                "My Creator",
                new Date("May", 6, 2000),
                "Male",
                1);

        Politician trudeau = new Politician(
                "Justin Trudeau",
                new Date("December", 25, 1971),
                "Male",
                "Liberal",
                0.25);

        Singer dion = new Singer(
                "Celine Dion",
                new Date("March", 30, 1961),
                "Female",
                "La voix du bon Dieu",
                new Date("November", 6, 1981),
                0.5);

        // Add entities to the game
        addEntity(usa);
        addEntity(myCreator);
        addEntity(trudeau);
        addEntity(dion);
    }

    public void addEntity(Entity entity) {
        entities[numOfEntities++] = entity.clone();
    }

    public int genRandomEntityId() {
        return (int)(Math.random() * numOfEntities);
    }

    public void changeEntity() {
        entityid = genRandomEntityId();
        Entity entity = entities[entityid];
        entName = entity.getName();

        // Update UI
        entityName.setText(entName);
        userIn.getText().clear();
        imageSetter();
        welcomeToGame(entity);
    }

    public void imageSetter() { //Image display for all the entities
        Entity entity = entities[entityid];
        String name = entity.getName();

        //Loads images from drawable folder
        if (name.equals("United States")) {
            entityImage.setImageResource(R.drawable.usaflag);
        } else if (name.equals("Justin Trudeau")) {
            entityImage.setImageResource(R.drawable.justint);
        } else if (name.equals("Celine Dion")) {
            entityImage.setImageResource(R.drawable.celidion);
        }
    }

    public void welcomeToGame(Entity entity) {
        AlertDialog.Builder welcomeAlert = new AlertDialog.Builder(GuessMaster.this);
        welcomeAlert.setTitle("GuessMaster Game v3");
        welcomeAlert.setMessage(entity.welcomeMessage());
        welcomeAlert.setCancelable(false);

        welcomeAlert.setNegativeButton("START GAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Game is Starting... Enjoy", Toast.LENGTH_SHORT).show();
            }
        });

        welcomeAlert.create().show();
    }

    public void playGame(Entity entity) {
        answer = userIn.getText().toString();
        answer = answer.replace("\n", "").replace("\r", "");

        try {
            Date date = new Date(answer);

            if (date.precedes(entity.getBorn())) {
                showAlert("Incorrect", "Try a later date.");
            } else if (entity.getBorn().precedes(date)) {
                showAlert("Incorrect", "Try an earlier date.");
            } else {
                // Correct guess
                currentTicketWon = entity.getAwardedTicketNumber();
                totalTicketNum += currentTicketWon;
                ticketsum.setText("Total Tickets: " + totalTicketNum); //Ticket view that is live each time

                showWinAlert(entity);
            }
        } catch (Exception e) {
            showAlert("Error", "Please enter a valid date (mm/dd/yyyy)");
        }
    }

    private void showAlert(String title, String message) { //Android Buttons
        AlertDialog.Builder alert = new AlertDialog.Builder(GuessMaster.this);
        alert.setTitle(title); //Answer for the prompt like corrrect
        alert.setMessage(message); //Message to help user if wrong answer
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() { //Button
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    private void showWinAlert(Entity entity) {
        AlertDialog.Builder winAlert = new AlertDialog.Builder(GuessMaster.this);
        winAlert.setTitle("You Won");
        winAlert.setMessage("BINGO! " + entity.closingMessage());
        winAlert.setCancelable(false);

        winAlert.setNegativeButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                continueGame();
            }
        });

        winAlert.create().show();
    }

    public void continueGame() { //Random Function from Instructions
        entityid = genRandomEntityId();
        Entity entity = entities[entityid];
        entName = entity.getName();

        imageSetter();
        entityName.setText(entName);
        userIn.getText().clear();
    }
}

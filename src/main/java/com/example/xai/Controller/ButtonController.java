package com.example.xai.Controller;


import com.example.xai.Model.Construction.HyperNEAT;
import com.example.xai.Model.Construction.NEAT;
import com.example.xai.Model.Construction.TrainingService;
import com.example.xai.View.ConfigView;
import com.example.xai.View.InteractiveMainView;
import javafx.scene.control.Button;

public class ButtonController {

    private final TrainingService trainingService;
    private final InteractiveMainView mainView;

    public ButtonController(Object neat, InteractiveMainView mainView) {
        this.mainView = mainView;

        if (neat instanceof NEAT) {
            this.trainingService = new TrainingService((NEAT) neat);
        } else if (neat instanceof HyperNEAT) {
            this.trainingService = new TrainingService((HyperNEAT) neat);
        } else {
            throw new IllegalArgumentException("Unsupported training type");
        }

        //mainView.getPlayButton().setOnAction(e -> handlePlayButton());
        mainView.getNextButton().setOnAction(e -> handleNextButton());
        mainView.getBackButton().setOnAction(e -> handleBackButton());

        trainingService.start();
    }


    private void handleNextButton() {
        mainView.getNextButton().setDisable(true);
        trainingService.nextStep();
    }

    private void handleBackButton() {
        ConfigView configView = new ConfigView();
        ConfigController configController = new ConfigController(configView, mainView.getMainApp());

        // Zurück zum ConfigView
        mainView.getRoot().setCenter(configView.getRoot());
    }
}

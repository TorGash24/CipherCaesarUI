package com.example.caesarproject;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXRadioButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Controller {

    @FXML
    private TextField inputPathTextField;

    @FXML
    private TextField keyTextField;

    @FXML
    private Label statusMessageLabel;
    @FXML
    private JFXRadioButton cipherRadioButton;

    @FXML
    private JFXRadioButton unCipherRadioButton;

    @FXML
    private JFXRadioButton bruteForceRadioButton;

    @FXML
    private StackPane stackPaneDialog;

    public void clickOnStartButton() {
        if (inputPathTextField.getText().isEmpty()) {
            statusMessageLabel.setText("Вы не указали путь!");
        } else {
            Path path = Path.of(inputPathTextField.getText());
            if (!Files.exists(path)) {
                statusMessageLabel.setText("Файл не был найден, повторите попытку снова!");
            } else {
                String textFromFile = "";
                try {
                    textFromFile = CipherUtils.getTextFromFile(path);
                    if (!cipherRadioButton.isSelected() && !unCipherRadioButton.isSelected() && !bruteForceRadioButton.isSelected()) {
                        statusMessageLabel.setText("Выберите действие!");
                    } else if (cipherRadioButton.isSelected()) {
                        if (keyTextField.getText().isEmpty()) {
                            statusMessageLabel.setText("Введите ключ!");
                        } else {
                            int key = Integer.parseInt(keyTextField.getText());
                            try {
                                String cipheredText = CipherUtils.codingText(textFromFile, key);
                                CipherUtils.recordingInFile(path, cipheredText, key, "_cipheredText");
                                statusMessageLabel.setText("Шифрование с ключем  "+ key +  ", успешно завершено!");
                            } catch (BusinessException e) {
                                statusMessageLabel.setText(e.getMessage());
                            }
                        }
                    } else if (unCipherRadioButton.isSelected()) {
                        if (keyTextField.getText().isEmpty()) {
                            statusMessageLabel.setText("Введите ключ!");
                        } else {
                            try {
                                int key = Integer.parseInt(keyTextField.getText());
                                String unCipherText = CipherUtils.unCodingText(textFromFile, key);
                                CipherUtils.recordingInFile(path, unCipherText, key, "_unCipheredText");
                                statusMessageLabel.setText("Расшифровка с ключем " + key + ", успешно завершена!");
                            } catch (BusinessException e) {
                                statusMessageLabel.setText(e.getMessage());
                            }
                        }
                    } else if (bruteForceRadioButton.isSelected()) {
                        String bruteForceText = CipherUtils.getBruteforce(textFromFile);
                        if (bruteForceText.length() == 0) {
                            statusMessageLabel.setText("Не удалось взломать, измените текст!");
                        } else {
                            CipherUtils.recordingInFile(path, bruteForceText, 0, "_BruteForceText");
                            statusMessageLabel.setText("Взлом успешно завершен!");
                        }
                    }
                } catch (BusinessException e) {
                    statusMessageLabel.setText(e.getMessage());
                }

            }
        }
    }

    public void anchorPaneRootDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void anchorPaneRootDragDrop(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
            File file = dragEvent.getDragboard().getFiles().get(0);
            inputPathTextField.setText(file.getAbsolutePath());
        }
    }

    public void resetButton() {
        inputPathTextField.setText("");
        cipherRadioButton.setSelected(false);
        unCipherRadioButton.setSelected(false);
        bruteForceRadioButton.setSelected(false);

        statusMessageLabel.setText("Выполнен сброс");
    }
    public void groupToggle() {
        ToggleGroup tGroup = new ToggleGroup();
        cipherRadioButton.setToggleGroup(tGroup);
        unCipherRadioButton.setToggleGroup(tGroup);
        bruteForceRadioButton.setToggleGroup(tGroup);
    }
}
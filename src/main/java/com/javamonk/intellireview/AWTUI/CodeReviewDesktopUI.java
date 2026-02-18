package com.javamonk.intellireview.AWTUI;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CodeReviewDesktopUI {
    public static void main(String[] args) {
        Frame frame = new Frame("Java Monk IntelliReview");

        TextArea codeInput = new TextArea();
        Button analyzeButton = new Button("Analyze Code");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);

        frame.setLayout(new BorderLayout());
        frame.add(codeInput, BorderLayout.NORTH);
        frame.add(analyzeButton, BorderLayout.CENTER);
        frame.add(resultArea, BorderLayout.SOUTH);

        analyzeButton.addActionListener(e -> {
            try {
                String code = codeInput.getText();
                URL url = new URL("http://localhost:8080/api/v1/code-review/analyze");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();

                resultArea.setText(response.toString() + "\r\n" + code);

                //resultArea.setText(response);


                conn.disconnect();
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}

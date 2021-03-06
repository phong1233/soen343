package org.team4.shhParameters;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.team4.common.Coordinate;
import org.team4.common.Settings;
import org.team4.common.TimePeriod;
import org.team4.permissions.Permission;
import org.team4.user.User;
import org.team4.user.UserService;
import org.team4.common.exceptions.InvalidTimeEntryException;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class ShhParameterController {

    private ZoneService zoneService;

    @FXML
    public TableView zoneTable;
    public TableColumn<Zone, String> nameColumn;
    public TableColumn<Zone, String> defaultTempColumn;
    public TableColumn<Zone, String> timePeriod1Column;
    public TableColumn<Zone, String> timePeriod2Column;
    public TableColumn<Zone, String> timePeriod3Column;

    //Add Zone variables
    public TextField addTemp;
    public TextField addName;
    public Button addButton;
    public Text addNameError;
    public Text addTempError;

    //Zone ChoiceBox
    public ChoiceBox<String> zoneChoiceBox;

    //Edit zone variables
    public TextField editTemp;
    public Button setTemp;
    public Text editTempError;
    public TextField periodBegin1;
    public TextField periodBegin2;
    public TextField periodBegin3;
    public TextField periodEnd1;
    public TextField periodEnd2;
    public TextField periodEnd3;
    public TextField periodTemp1;
    public TextField periodTemp2;
    public TextField periodTemp3;
    public Button setPeriodTemp1;
    public Button setPeriodTemp2;
    public Button setPeriodTemp3;
    public Text editPeriodError1;
    public Text editPeriodError2;
    public Text editPeriodError3;
    public Button deleteZone;
    public Text deleteError;

    //add room to zone
    public Button addRoom;
    public ChoiceBox<String> roomsChoiceBox;
    public ChoiceBox<String> addZoneChoiceBox;

    //request room
    public Button requestRoom;
    public ChoiceBox<String> roomsChoiceBox1;

    //Override room temp
    public ChoiceBox<String> roomChoiceBox2;
    public TextField overrideTemp;
    public Text overrideError;
    public Button overrideButton;

    //Set seasonal temperature
    public TextField summerTemp;
    public TextField winterTemp;
    public Button seasonTempButton;
    public Text seasonTempError;

    //Temperature alert threshold
    public TextField upperBound;
    public TextField lowerBound;
    public Button thresholdButton;
    public Text thresholdError;

    /**
     * Controller
     */
    public ShhParameterController() {
        zoneService = new ZoneService();
    }

    public static UserService userService = new UserService();

    /**
     * Initial method
     */
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        defaultTempColumn.setCellValueFactory(new PropertyValueFactory<>("defaultTemp"));
        timePeriod1Column.setCellValueFactory(new PropertyValueFactory<>("timePeriod1"));
        timePeriod2Column.setCellValueFactory(new PropertyValueFactory<>("timePeriod2"));
        timePeriod3Column.setCellValueFactory(new PropertyValueFactory<>("timePeriod3"));
        displayAllZones();
        resetErrorMessages();
    }

    /**
     * Resets all the error message
     */
    public void resetErrorMessages() {
        addNameError.setText("");
        addTempError.setText("");
        editTempError.setText("");
        editPeriodError1.setText("");
        editPeriodError2.setText("");
        editPeriodError3.setText("");
        deleteError.setText("");
        overrideError.setText("");
        seasonTempError.setText("");
        thresholdError.setText("");
    }

    /**
     * Display all of the rooms in choice box
     */
    public void displayAllRooms() {
        ArrayList<Coordinate> allRooms = zoneService.getAllIndoorRooms();
        roomsChoiceBox.getItems().clear();
        roomsChoiceBox1.getItems().clear();
        roomChoiceBox2.getItems().clear();
        for(Coordinate coord : allRooms) {
            roomsChoiceBox.getItems().add(coord.toString());
            roomsChoiceBox1.getItems().add(coord.toString());
            roomChoiceBox2.getItems().add(coord.toString());
        }
    }

    /**
     * Displays all the zones in the table and choice box
     */
    public void displayAllZones() {
        HashSet<Zone> allZones = zoneService.getAllZones();
        zoneTable.getItems().clear();
        zoneChoiceBox.getItems().clear();
        addZoneChoiceBox.getItems().clear();
        for(Zone z : allZones) {
            zoneTable.getItems().add(z);
            zoneChoiceBox.getItems().add(z.name);
            zoneChoiceBox.setValue(z.name);
            addZoneChoiceBox.getItems().add(z.name);
        }
    }

    /**
     * Handle action to add new zone
     */
    public void handleAddNewZone() {
        resetErrorMessages();
        String name = addName.getText();
        String tempStr = addTemp.getText();
        boolean valid = true;
        if(!zoneService.verifyName(name)) {
            valid = false;
            addNameError.setText("Invalid");
        }

        Double temp = zoneService.verifyTemp(tempStr);
        if(temp == null) {
            valid = false;
            addTempError.setText("Invalid");
        }
        if(!valid) return;
        zoneService.addNewZone(name, temp);
        addName.setText("");
        addTemp.setText("");
        displayAllZones();
    }

    /**
     * Enable or disable all edit buttons
     * @param disable
     */
    public void toggleAllEditButton(boolean disable) {
        setPeriodTemp1.setDisable(disable);
        setPeriodTemp2.setDisable(disable);
        setPeriodTemp3.setDisable(disable);
        setTemp.setDisable(disable);
        deleteZone.setDisable(disable);
        verifyCurrentUserSHHPermission();
    }

    /**
     * updates edit depending on zone in choice box
     */
    public void handleZoneChoiceBox() {
        String selectedZone = zoneChoiceBox.getValue();
        if(selectedZone == null) {
            toggleAllEditButton(true);
            return;
        }
        toggleAllEditButton(false);
        Zone z = zoneService.getZone(selectedZone);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        TimePeriod tp1 = z.timePeriod1;
        TimePeriod tp2 = z.timePeriod2;
        TimePeriod tp3 = z.timePeriod3;
        periodBegin1.setText(tp1 == null ? "" : timeFormat.format(tp1.begin));
        periodTemp1.setText(tp1 == null ? "" : Double.toString(tp1.desiredTemperature));
        periodBegin2.setText(tp2 == null ? "" : timeFormat.format(tp2.begin));
        periodTemp2.setText(tp2 == null ? "" : Double.toString(tp2.desiredTemperature));
        periodBegin3.setText(tp3 == null ? "" : timeFormat.format(tp3.begin));
        periodTemp3.setText(tp3 == null ? "" : Double.toString(tp3.desiredTemperature));
        periodEnd1.setText(tp1 == null ? "" : timeFormat.format(tp1.end));
        periodEnd2.setText(tp2 == null ? "" : timeFormat.format(tp2.end));
        periodEnd3.setText(tp3 == null ? "" : timeFormat.format(tp3.end));
        double temp = z.defaultTemp;
        editTemp.setText(Double.toString(temp));
    }

    /**
     * Edit the temperature
     */
    public void handleEditTemperature() {
        resetErrorMessages();
        String temp = editTemp.getText();
        String zone = zoneChoiceBox.getValue();
        Double newTemp = zoneService.verifyTemp(temp);
        if(newTemp == null) {
            editTempError.setText("X");
            return;
        }
        zoneService.editZoneTemperature(zone, newTemp);
        displayAllZones();
    }

    /**
     * Set the time period 1 of zone
     */
    public void handleSetPeriod1(){
        resetErrorMessages();
        String zone = zoneChoiceBox.getValue();
        String b = periodBegin1.getText();
        String e = periodEnd1.getText();
        String temp = periodTemp1.getText();
        Date begin;
        Date end;
        try {
            begin = zoneService.validateTimeEntry(b);
            end = zoneService.validateTimeEntry(e);
        } catch (InvalidTimeEntryException invalidTimeEntryException) {
            editPeriodError1.setText("X");
            return;
        }
        Double newTemp = zoneService.verifyTemp(temp);
        if(begin == null || end == null || newTemp == null) {
            editPeriodError1.setText("X");
            return;
        }
        zoneService.setTimePeriod1(zone, begin, end, newTemp);
        displayAllZones();
    }

    /**
     * Set the time period 2 of zone
     */
    public void handleSetPeriod2(){
        resetErrorMessages();
        String zone = zoneChoiceBox.getValue();
        String b = periodBegin2.getText();
        String e = periodEnd2.getText();
        String temp = periodTemp2.getText();
        Date begin;
        Date end;
        try {
            begin = zoneService.validateTimeEntry(b);
            end = zoneService.validateTimeEntry(e);
        } catch (InvalidTimeEntryException invalidTimeEntryException) {
            editPeriodError2.setText("X");
            return;
        }
        Double newTemp = zoneService.verifyTemp(temp);
        if(begin == null || end == null || newTemp == null) {
            editPeriodError2.setText("X");
            return;
        }
        zoneService.setTimePeriod2(zone, begin, end, newTemp);
        displayAllZones();
    }

    /**
     * Set the time period 3 of zone
     */
    public void handleSetPeriod3(){
        resetErrorMessages();
        String zone = zoneChoiceBox.getValue();
        String b = periodBegin3.getText();
        String e = periodEnd3.getText();
        String temp = periodTemp3.getText();
        Date begin;
        Date end;
        try {
            begin = zoneService.validateTimeEntry(b);
            end = zoneService.validateTimeEntry(e);
        } catch (InvalidTimeEntryException invalidTimeEntryException) {
            editPeriodError3.setText("X");
            return;
        }
        Double newTemp = zoneService.verifyTemp(temp);
        if(begin == null || end == null || newTemp == null) {
            editPeriodError3.setText("X");
            return;
        }
        zoneService.setTimePeriod3(zone, begin, end, newTemp);
        displayAllZones();

    }

    /**
     * Delete a zone
     */
    public void handleDeleteZone() {
        resetErrorMessages();
        String zone = zoneChoiceBox.getValue();
        boolean success = zoneService.deleteZone(zone);
        if(success) {
            displayAllZones();
            return;
        }
        deleteError.setText("Failed to delete zone");
    }

    /**
     * Add room to a zone
     */
    public void handleAddRoom() {
        resetErrorMessages();
        String room = roomsChoiceBox.getValue();
        String zone = addZoneChoiceBox.getValue();
        if(room == null || zone == null) return;
        Coordinate coord = new Coordinate(room);
        zoneService.setRoomZone(zone, coord.x, coord.y);
    }

    /**
     * Get room temperature
     */
    public void handleGetRoomTemp() {
        resetErrorMessages();
        String room = roomsChoiceBox1.getValue();
        if(room == null) return;
        zoneService.requestRoomTemperature(new Coordinate(room));
    }

    /**
     * Overwrite room temperature
     */
    public void handleOverrideTemperature() {
        resetErrorMessages();
        String room = roomChoiceBox2.getValue();
        if (room != null) {
            Coordinate coord = new Coordinate(room);
            Double temp = zoneService.verifyTemp(overrideTemp.getText());
            if(temp == null || !checkChangeTempPermission(coord.getX(), coord.getY())) {
                overrideError.setText("X");
                return;
            }
            zoneService.overrideTemperature(coord, temp);
        }
    }

    /**
     * Sets the seasonal temperature
     */
    public void handleSetSeasonTemperature() {
        resetErrorMessages();
        try {
            double sum = Double.parseDouble(summerTemp.getText());
            double win = Double.parseDouble(winterTemp.getText());
            zoneService.setSeasonalTemperature(sum, win);
        }
        catch (Exception e) {
            seasonTempError.setText("X");
        }
    }

    /**
     * Sets the temperature alert thresholds
     */
    public void handleSetTemperatureThreshold() {
        resetErrorMessages();
        try {
            double upper = Double.parseDouble(upperBound.getText());
            double lower = Double.parseDouble(lowerBound.getText());
            zoneService.setTemperatureAlertThreshold(upper, lower);
        }
        catch (Exception e) {
            thresholdError.setText("X");
        }
    }

    /**
     * Check if a user has permission to edit SHH.
     * @return true if current user has permissions to edit SHH or false if not.
     */
    public boolean checkSHHPermission() {
        User user = userService.getSingleUser(Settings.currentUser);
        return Permission.checkSHHPermission(user);
    }

    /**
     *
     *  Checks if a user has permission to change the temperature of a certain room.
     * @param x The x coordinate of the room the user is trying to modify.
     * @param y The y coordinate of the room the user is trying to modify.
     * @return boolean true if the user is allowed to modify room temperature or false if not.
     */
    public boolean checkChangeTempPermission(int x, int y) {
        User user = userService.getSingleUser(Settings.currentUser);
        return Permission.checkChangeTempPermission(user,x,y);
    }

    /**
     * Verifies if the user has permission to modify and access SHH. If the user has permission, the panel is accessible, if the user does not have permission, the panel is disabled.
     */
    public void verifyCurrentUserSHHPermission() {
        if (!checkSHHPermission()) {
            disableSHHPanel(true);
        } else {
            disableSHHPanel(false);
        }
    }

    /**
     *  Disables or make the SHH panel available.
     * @param bool true, the panel will be disabled except for the temperature override. false, the panel will not be disabled.
     */
    public void disableSHHPanel(boolean bool) {

        zoneTable.setDisable(bool);
        addTemp.setDisable(bool);
        addName.setDisable(bool);
        addButton.setDisable(bool);

        //Zone ChoiceBox
        zoneChoiceBox.setDisable(bool);

        //Edit zone variables
        editTemp.setDisable(bool);
        setTemp.setDisable(bool);
        periodBegin1.setDisable(bool);
        periodBegin2.setDisable(bool);
        periodBegin3.setDisable(bool);
        periodEnd1.setDisable(bool);
        periodEnd2.setDisable(bool);
        periodEnd3.setDisable(bool);
        periodTemp1.setDisable(bool);
        periodTemp2.setDisable(bool);
        periodTemp3.setDisable(bool);
        setPeriodTemp1.setDisable(bool);
        setPeriodTemp2.setDisable(bool);
        setPeriodTemp3.setDisable(bool);
        editPeriodError1.setDisable(bool);
        editPeriodError2.setDisable(bool);
        editPeriodError3.setDisable(bool);
        deleteZone.setDisable(bool);

        //add room to zone
        addRoom.setDisable(bool);
        roomsChoiceBox.setDisable(bool);
        addZoneChoiceBox.setDisable(bool);

        //request room
        requestRoom.setDisable(bool);
        roomsChoiceBox1.setDisable(bool);

        //Set seasonal temperature
        summerTemp.setDisable(bool);
        winterTemp.setDisable(bool);
        seasonTempButton.setDisable(bool);

        //Temperature alert threshold
        upperBound.setDisable(bool);
        lowerBound.setDisable(bool);
        thresholdButton.setDisable(bool);
    }
}

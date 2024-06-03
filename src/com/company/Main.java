package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        //hash map for stops has stop ID as key and stop name as value
        HashMap<Integer, String> stops = new HashMap<>();
        //hash map for stop times has route ID as key and times as value
        HashMap<Integer, ArrayList<String>> stopTimes = new HashMap<>();
        //reading stops.txt file
        try {
            File file = new File("src\\gtfs\\stops.txt");
            Scanner stopsReader = new Scanner(file);
            //skips first line
            String data = stopsReader.nextLine();
            while (stopsReader.hasNextLine()) {
                data = stopsReader.nextLine();
                //splits string into array
                String[] stopsArray = data.split(",");
                //first data is stop ID, third is stop name
                stops.put(Integer.parseInt(stopsArray[0]), stopsArray[2]);
            }
            stopsReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //arguments: stop ID, number of next buses and time format
        int id = Integer.parseInt(args[0]);
        int busCount = Integer.parseInt(args[1]);
        String timeFormat = args[2];
        //check whether ID exists
        if (!stops.containsKey(id)) {
            System.out.println("Error: key does not exist!");
        }
        //third format has to be either relative or absolute (time format)
        else if (timeFormat.equals("relative") || timeFormat.equals("absolute")) {
            System.out.println("Bus stop name: " + stops.get(id));
            try {
                //reading stop_times.txt file
                File file = new File("src\\gtfs\\stop_times.txt");
                Scanner stopTimesReader = new Scanner(file);
                String data = stopTimesReader.nextLine();
                while (stopTimesReader.hasNextLine()) {
                    data = stopTimesReader.nextLine();
                    String[] stopTimesArray = data.split(",");
                    //fourth data is stop ID, checks if ID matches input ID
                    if (Integer.parseInt(stopTimesArray[3]) == id) {
                        //creates array list if key for route ID does not yet exist
                        //substring matches route ID
                        if (!stopTimes.containsKey(Integer.parseInt(stopTimesArray[0].substring(10, 13)))) {
                            stopTimes.put(Integer.parseInt(stopTimesArray[0].substring(10, 13)),
                                    new ArrayList<>());
                        }
                        //if key already exists, add time in the list
                        stopTimes.get(Integer.parseInt(stopTimesArray[0].substring(10, 13)))
                                .add(stopTimesArray[1].substring(0, 5));

                    }
                }
                stopTimesReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final int[] count = {0};
            //defining current time
            String currentTime = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            //converting current time into minutes
            int currentMinutes = Integer.parseInt(currentTime.substring(0, 2)) * 60
                    + Integer.parseInt(currentTime.substring(3, 5));
            System.out.println("Current time: " + currentTime);
            //iterating stop times hash map
            stopTimes.forEach((key, value) -> {
                System.out.print(key + ": ");
                //sorting times
                Collections.sort(value);
                for (String s : value) {
                    //converting time into minutes
                    int minutes = Integer.parseInt(s.substring(0, 2)) * 60
                            + Integer.parseInt(s.substring(3, 5));
                    //checks whether bus time is greater or equal to current time but not greater than 2 hours
                    if (minutes >= currentMinutes && minutes < currentMinutes + 120) {
                        //if time format is absolute, prints times in HH:mm format
                        if (timeFormat.equals("absolute"))
                            System.out.print(s + " ");
                        //if time format is relative, prints times in minutes
                        else
                            System.out.print((minutes - currentMinutes) + "min ");
                        count[0]++;
                    }
                    //if counter exceeds number of buses, breaks loop
                    if (count[0] > busCount)
                        break;
                }
                //resets counter
                count[0] = 0;
                System.out.println();
            });
        } else {
            System.out.println("Error: invalid time format!");
        }
    }
}
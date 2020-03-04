import java.io.*;
import java.util.*;


class Implementation {

    // Initial strings from file.
    static HashSet<String> ungroupedStrings = new HashSet<String>();

    // Maps have appropriate strings' elements as keys and sets of appropriate strings with them
    // as values.
    static HashMap<String, HashSet<String>> mapFirst = new HashMap<String, HashSet<String>>();
    static HashMap<String, HashSet<String>> mapSecond = new HashMap<String, HashSet<String>>();
    static HashMap<String, HashSet<String>> mapThird = new HashMap<String, HashSet<String>>();

    // Array of result groups of strings.
    static ArrayList<ArrayList<String>> resultGroupsOfStrings = new ArrayList<ArrayList<String>>();

    // Group of strings of current search.
    static ArrayList<String> currentGroup = new ArrayList<String>();


    public static void main(String[] args) {
		try {
		Implementation obj = new Implementation();
		obj.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run(String[] args) throws Exception {
		
        // Read the strings.
        String stringToAdd = null;

        FileReader fr = null;
        BufferedReader br = null;

        int semicolon1 = 0;
        int semicolon2 = 0;
        String substring1 = null;
        String substring2 = null;
        String substring3 = null;

        // "Try" block reads the file, checks the strings, closes the file.
        try {
            fr = new FileReader("C:/lng.csv");
            br = new BufferedReader(fr);

            while ((stringToAdd = br.readLine()) != null) {
                if (canAdd(stringToAdd)) {
                    ungroupedStrings.add(stringToAdd);

                    semicolon1 = stringToAdd.indexOf(';');
                    semicolon2 = stringToAdd.lastIndexOf(';');

                    substring1 = stringToAdd.substring(0, semicolon1);
                    substring2 = stringToAdd.substring(semicolon1 + 1, semicolon2);
                    substring3 = stringToAdd.substring(semicolon2 + 1);

                    mapFirst.putIfAbsent(substring1, new HashSet<String>());
                    mapSecond.putIfAbsent(substring2, new HashSet<String>());
                    mapThird.putIfAbsent(substring3, new HashSet<String>());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fill the maps.
        HashSet<String> stringSet = null;
        String stringUngrouped = null;
        Iterator<String> iteratorUngrouped = ungroupedStrings.iterator();

        while (iteratorUngrouped.hasNext()) {

            stringUngrouped = iteratorUngrouped.next();

            semicolon1 = stringUngrouped.indexOf(';');
            semicolon2 = stringUngrouped.lastIndexOf(';');

            substring1 = stringUngrouped.substring(0, semicolon1);
            substring2 = stringUngrouped.substring(semicolon1 + 1, semicolon2);
            substring3 = stringUngrouped.substring(semicolon2 + 1);

            stringSet = mapFirst.get(substring1);
            stringSet.add(stringUngrouped);
            mapFirst.put(substring1, stringSet);

            stringSet = mapSecond.get(substring2);
            stringSet.add(stringUngrouped);
            mapSecond.put(substring2, stringSet);

            stringSet = mapThird.get(substring3);
            stringSet.add(stringUngrouped);
            mapThird.put(substring3, stringSet);
        }


        // Group the strings: start new group, start an iteration of search.
        // When recoursion is ended, add group to results, proceed with a next key of a mapFirst.
        Iterator<Map.Entry<String, HashSet<String>>> iterator1 = mapFirst.entrySet().iterator();
        while (iterator1.hasNext()) {
            currentGroup = null;
            currentGroup = new ArrayList<String>();
            Map.Entry<String, HashSet<String>> mapEntry = iterator1.next();

            findAllStringsOfTheKind(mapFirst, mapEntry.getKey());

            if (currentGroup.size() > 1) {
                resultGroupsOfStrings.add(currentGroup);
            }
        }


        // Sort output strings.
        Collections.sort(resultGroupsOfStrings, new comparatorOfArrayListsInArrayList());


        // Output to file.
        FileWriter fw1 = null;
        BufferedWriter bw1 = null;

        try {
            fw1 = new FileWriter("C:/lng2.csv");
            bw1 = new BufferedWriter(fw1);

            bw1.write("Number of groups with elements quantity more than 1:");
            bw1.newLine();
            bw1.write(resultGroupsOfStrings.size());
            bw1.newLine();

            ArrayList<String> groupToOutput = new ArrayList<String>();
            for (int i = 0; i < resultGroupsOfStrings.size(); i++) {
                bw1.newLine();
                bw1.newLine();
                bw1.write("Group ");
                bw1.write(i+1);
                groupToOutput = resultGroupsOfStrings.get(i);
                for (String stringToOutput : groupToOutput) {
                    bw1.newLine();
                    bw1.write(stringToOutput);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bw1.flush();
            fw1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    // String check, whether it fits the requirement.
    public static boolean canAdd(String stringPar) {

        if (stringPar.length() == 0) {
            return false;
        }

        // If element is absent and is replaced with space, miss string.
        if (stringPar.indexOf(' ') != -1) {
            return false;
        }

        // If the string element is finished by semicolon, miss string. It couldn't be split right.
        // Otherwise, split it by semicolon and proceed, but only if sub-elements are three.
        if (stringPar.charAt(stringPar.length() - 1) == ';') {
            return false;
        }
        String[] splitString = stringPar.split(";");

        if (splitString.length == 3) {
            // If the string element is smaller then 2 chars (even not ""), miss string.
            // Else, if element doesn't start and end with ", miss string.
            // Else, if the element itself contains ", besides first and last symbols, miss string.
            // Returns true, if passes.

            for (int i = 0; i < 3; i++) {
                if (splitString[i].length() >= 2) {
                    if (!(splitString[i].startsWith("\"") && splitString[i].endsWith("\""))) {
                        return false;
                    } else {
                        if (splitString[i].length() > 2) {
                            splitString[i] = splitString[i].substring(1, splitString[i].length()-1);
                            if (splitString[i].indexOf('"') != -1) {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }



    // Recoursive of the search among the maps. It handles a map and a key, takes it's strings
    // and for each string performes new search. If meets already grouped string, misses it.
    public void findAllStringsOfTheKind(HashMap<String, HashSet<String>> mapHandled,
                                               String mapKeyHandled) {

        HashSet<String> stringSet1 = mapHandled.get(mapKeyHandled);

        for (String string1 : stringSet1) {
            if (ungroupedStrings.contains(string1)) {
                currentGroup.add(string1);
                ungroupedStrings.remove(string1);

                findAllStringsOfTheKind(mapFirst, string1.substring(0, string1.indexOf(';')));
                findAllStringsOfTheKind(mapSecond, string1.substring(string1.indexOf(';') + 1, string1.lastIndexOf(';')));
                findAllStringsOfTheKind(mapThird, string1.substring(string1.lastIndexOf(';') + 1));
            } else {
                continue;
            }
        }
        return;
    }
}
	
	
	
class comparatorOfArrayListsInArrayList implements Comparator<ArrayList<String>> {
    @Override
    public int compare (ArrayList<String> al1, ArrayList<String> al2) {
        if (al1.size() > al2.size()) {
            return 1;
        } else if (al1.size() < al2.size()) {
            return -1;
        } else {
            return 0;
        }
    }
}
import java.io.*;
import java.util.*;

public class Main {
	private static HashMap<String, User> users = new HashMap<String, User>();

    private static class User implements Comparable<User> {
        String name = "";
        String email = "";
        String message = "";
        String present = "";
        String random = "";
        String[] potentials = new String[5];

        public void print() {
            System.out.println(name + ": '" + potentials[0] + "' '" + potentials[1] + "' '"  + potentials[2] + "' '" + potentials[3] + "' " + potentials[4]);
        }

        public int compareTo(User compareUser) {
	
			String compareName = ((User) compareUser).name;

			return this.name.compareTo(compareName);
		}
    }

    public static void createMap (BufferedReader file) {
		String line = "";
		boolean skipFirst = true;
		try {
			while((line = file.readLine()) != null) {
				if(skipFirst) {
					skipFirst = false;
				}
				else {
					String delims = ",";
					String[] tokens = line.split(delims);
					User newUser = new User();
					newUser.name = tokens[1].toUpperCase();
					newUser.email = tokens[2];
					for(int i = 1; i <= 5; i++) {
						newUser.potentials[i - 1] = tokens[2 + i].toUpperCase();
					}
					newUser.message = tokens[8];
					newUser.present = tokens[9];
					newUser.random = tokens[10];
					users.put(newUser.name, newUser);
				}
			}

            file.close(); 
		}
		catch(IOException ex) {
			System.out.println("Error reading file.");
		}
	}

	public static void findMatches (BufferedWriter emails, BufferedWriter options, BufferedWriter random, BufferedWriter random_options) {
		for(String name : users.keySet()) {
			User temp = users.get(name);
			boolean found_match = false;
			for(int i = 0; i < 5; i++) {
				if (users.containsKey(temp.potentials[i])) {
					User potential = users.get(temp.potentials[i]);
					for(int j = 0; j < 5; j++) {
						if(potential.potentials[j].equals(temp.name) && !(potential.name.equals(temp.name))) {
							found_match = true;
							String content = "Name: " + potential.name + "\nEmail: " + potential.email + "\nMessage to recieve: " + temp.message + "\nPresent to recieve: " + temp.present + "\n" + "From: " + temp.name + "\n\n";
							try {
								emails.write(temp.email + ", ");
								options.write(content);
							}
							catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			if (!found_match && temp.random.equals("Yes")) {
				String content = "Name: " + temp.name + "\nEmail: " + temp.email + "\nMessage they chose: " + temp.message + "\nPresent they chose: " + temp.present + "\n\n";
				try {
					random.write(temp.email + ", ");
					random_options.write(content);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (!found_match && temp.random.equals("No")) {
				System.out.println("not random");
			}
		}
	}

	public static void main(String [] args)
	{
		String emailfile = "";
		String randomfile = "";
		String optionsfile = "";
		String optionsrandomfile = "";
		String txtfile = "";
		boolean terminate = false;
		int count = 0;

		for(int i = 0; i < args.length; i++) {
			count++;
			if(i == 0) {
				txtfile = args[i];
			}
			else if(i == 1) {
				emailfile = args[i];
			}
			else if(i == 2) {
				optionsfile = args[i];
			}
			else if(i == 3) {
				randomfile = args[i];
			}
			else if(i == 4) {
				optionsrandomfile = args[i];
			}
			else {
				terminate = true;
				break;
			}
        }

        if (!terminate && count == 5) {
        	boolean fileValid = true;

        	// create logging files
        	try {
				File file_emails = new File(emailfile + ".txt");
				File file_options = new File(optionsfile + ".txt");
				File file_random = new File(randomfile + ".txt");
				File file_random_options = new File(optionsrandomfile + ".txt");

				// if file doesnt exist, then create it
				if (!file_emails.exists()) {
					file_emails.createNewFile();
				}
				if (!file_options.exists()) {
					file_options.createNewFile();
				}
				if (!file_random.exists()) {
					file_random.createNewFile();
				}
				if (!file_random_options.exists()) {
					file_random_options.createNewFile();
				}				

				// check if there is written permission
				if (file_emails.canWrite() && file_options.canWrite()) {

					FileWriter fw_emails = new FileWriter(file_emails.getAbsoluteFile());
					FileWriter fw_options = new FileWriter(file_options.getAbsoluteFile());
					FileWriter fw_random = new FileWriter(file_random.getAbsoluteFile());
					FileWriter fw_random_options = new FileWriter(file_random_options.getAbsoluteFile());
					BufferedWriter bw_emails = new BufferedWriter(fw_emails);
					BufferedWriter bw_options = new BufferedWriter(fw_options);
					BufferedWriter bw_random = new BufferedWriter(fw_random);
					BufferedWriter bw_random_options = new BufferedWriter(fw_random_options);

					// read in text file
		        	try {
		            	FileReader fileReader = new FileReader(txtfile);
		            	BufferedReader bufferedReader = new BufferedReader(fileReader);

		            	createMap(bufferedReader);        
		        	}
			        catch(FileNotFoundException ex) {
			            System.out.println( "Unable to open file '" + txtfile + "'");   
			            fileValid = false;             
			        }

			        if(fileValid) {
						findMatches(bw_emails, bw_options, bw_random, bw_random_options);

						bw_emails.close();
						bw_options.close();
						bw_random.close();
						bw_random_options.close();
					}
					else {
						System.out.println("Cannot Write");
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
        }
        else {
        	System.out.println("Arguments are incorrect. Terminating.");
        }
	}
}
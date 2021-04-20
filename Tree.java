import java.util.HashMap;
import java.io.File; 
import java.util.Arrays;

public class Tree { 
	public static HashMap<String, String> options = new HashMap<String, String>(); 
	public static HashMap<String, Integer> duplicate = new HashMap<String, Integer>();
	public static String ANSI_CYAN = "\u001B[36m"; //use for directories
	public static String ANSI_RED = "\u001B[31m"; // use for executables
	public static String ANSI_RESET = "\u001B[0m"; //use to reset the colour	 
	public static void main(String[] args) {
		options.put("dir", System.getProperty("user.dir"));
		options.put("-help", "false");
		options.put("-c", "true");
		options.put("-d", "false");
		options.put("-l", "1");
		options.put("-a", "false");
		duplicate.put("-help", 0);
		duplicate.put("-c", 0);
		duplicate.put("-d", 0);
		duplicate.put("-l", 0);
		duplicate.put("-a", 0);
		if(checkArgs(args)) {
			if (Boolean.valueOf(options.get("-help"))) {
				System.out.println("Usage: tree [dir] [-option [parameter]]");
				System.out.println("[dir]         :: directory to star traversal [.]");
				System.out.println("-help         :: display this help and exit [false]");
				System.out.println("-c true|false :: show entires colorized [true]");
				System.out.println("-d            :: list directories only [false]");
				System.out.println("-l n          :: maximum display depth [1]");
				System.out.println("-a            :: show hidden files [false]");
			} else {
				int depth = Integer.parseInt(options.get("-l"));
				File file = new File(options.get("dir"));
				File[] fileList = file.listFiles(); 
				if(!Boolean.valueOf(options.get("-c"))) {
					ANSI_CYAN = ANSI_RESET; 
					ANSI_RED = ANSI_RESET;
				}
				if(!file.isHidden() || (file.isHidden() && Boolean.valueOf(options.get("-a")))) {
					if (fileList != null) { //empty dir or a file
						Arrays.sort(fileList); //sort using ASCII
						for(File f : fileList) {
							if(f.isHidden() && !Boolean.valueOf(options.get("-a"))) {
								continue;
							} else {
								if(f.isDirectory()) {
									System.out.println(ANSI_CYAN + f.getName() + ANSI_RESET); 
									printFiles(f, depth-1);
								} else if(!Boolean.valueOf(options.get("-d"))) {
									if(f.canExecute()) {
										System.out.println(ANSI_RED + f.getName() + ANSI_RESET);
									} else {
										System.out.println(f.getName());
									}
								}
							}
						}
					} else { //empty dir, or just a file given (from main path)
						if(file.isDirectory()) {
							System.out.println(ANSI_CYAN + file.getName() + ANSI_RESET);
						} else if(!Boolean.valueOf(options.get("-d"))) {
							if(file.canExecute()) {
								System.out.println(ANSI_RED + file.getName() + ANSI_RESET);
							} else {
								System.out.println(file.getName()); 
							}
						}
					}
				}
			}
		} else {
			System.out.println("to see usage of Tree, type: ./tree -help");
		}
	}
	public static boolean checkArgs(String[] args) {
		boolean error = false; 
		for(int i = 0; i < args.length; i++) {
			if(!options.containsKey(args[i].toLowerCase()) && i > 0) { //not the first argument and not a valid option
				System.out.println("invalid argument given: " + args[i]); 
				error = true; 
			} else if(!options.containsKey(args[i].toLowerCase()) && i == 0) { //first arg could be a dir/file name
				File check = new File(args[i]); 
				if(check.exists()) {
					options.put("dir", args[i]); 	
				} else {
					if((args[i].charAt(0)) == ('-')) {
						System.out.println("provided option does not exist: " + args[i]);
					} else {
						System.out.println("file does not exist, please provide an existing directory");
					}	
					error = true;
				}
			} else if(args[i].equals("-help")) {
				options.put(args[i], "true");
				duplicate.put(args[i], duplicate.get(args[i]) + 1); 	
			} else if(args[i].equals("-c")) {
				duplicate.put(args[i], duplicate.get(args[i]) + 1); 

				if(i+1 == args.length) {
					System.out.println("no argument given, expected true or false"); 
					error = true; 
				} else {
					if(args[i+1].equals("true") || args[i+1].equals("false")) {
						options.put(args[i], args[i+1]);
						i++; 
					} else {
						System.out.println("invalid arguemnt given, expected true or false, given: " + args[i+1]);
						error = true;
						if(!options.containsKey(args[i+1].toLowerCase())) {
							i++; 
						}
					}
				}
			} else if(args[i].equals("-d")) {
				options.put(args[i], "true");
				duplicate.put(args[i], duplicate.get(args[i]) + 1); 	
			} else if((args[i].toLowerCase()).equals("-l")) {
				duplicate.put(args[i], duplicate.get(args[i].toLowerCase()) + 1);   
				if(i+1 == args.length) {
					System.out.println("no argument given, expected a number"); 
					error = true;
				} else {
					boolean skip = true;
					boolean num = true;	
					try {
						Integer.parseInt(args[i+1]); 
					} catch (Exception e) {
						System.out.println("invalid argument given, expected a number, given: " + args[i+1]);
						error = true;
						num = false;
						if(options.containsKey(args[i+1].toLowerCase())) {
							skip = false; 
						}
					}
					if(num && Integer.parseInt(args[i+1]) > 0) {
						options.put(args[i].toLowerCase(), args[i+1]);
					} else if(num && Integer.parseInt(args[i+1]) <= 0) {
						System.out.println("argument must be greater than 0 for argument -l");
						error = true;	
					}	
					if(skip) {
						i++;
					}
				}	
			} else if(args[i].equals("-a")) {
				options.put(args[i], "true"); 
				duplicate.put(args[i], duplicate.get(args[i]) + 1); 
			}
		}
		boolean duplicates = checkDuplicates(); 
		return !error && !duplicates;
	}
	public static void printFiles(File file, int depth) {
		if(depth > 0) {
			File newFile = new File(file.getPath());
			File[] fileList = newFile.listFiles();
			if(!newFile.isHidden() || (newFile.isHidden() && Boolean.valueOf(options.get("-a")))) {
				if (fileList != null) { //empty dir or a file
					Arrays.sort(fileList); //sort using ASCII
					for(File f : fileList) {
						if(f.isHidden() && !Boolean.valueOf(options.get("-a"))) {
							continue;
						} else {
							if(f.isDirectory()) {
								for(int j = Integer.parseInt(options.get("-l"))-depth; j > 1; j-- ) {
									System.out.print(" ");
								}
								System.out.println(Character.toString(0x2514) + ANSI_CYAN + f.getName() + ANSI_RESET);
								printFiles(f, depth-1);
							} else if(!Boolean.valueOf(options.get("-d"))) {
								for(int j = Integer.parseInt(options.get("-l"))-depth; j > 1; j--) {
									System.out.print(" ");
								}
								if(f.canExecute()) {
									System.out.println(Character.toString(0x2514) + ANSI_RED + f.getName() + ANSI_RESET);
								} else {
									System.out.println(Character.toString(0x2514) + f.getName());
								}
							}
						}
					}
				} else {
					if(newFile.isDirectory()) {
						for(int j = Integer.parseInt(options.get("-l"))-depth; j > 1; j--) {
							System.out.print(" ");
						}
						System.out.println(Character.toString(0x2514) + ANSI_CYAN + newFile.getName() + ANSI_RESET);
					} else if(!Boolean.valueOf(options.get("-d"))) {
						for(int j = Integer.parseInt(options.get("-l"))-depth; j > 1; j--) {
							System.out.print(" ");
						}
						if(newFile.canExecute()) {
							System.out.println(Character.toString(0x2514) + ANSI_RED + newFile.getName() + ANSI_RESET);
						} else {
							System.out.println(Character.toString(0x2514) + newFile.getName());
						}
					}
				}
			}
		}
	}
	public static boolean checkDuplicates () {
		boolean dup = false;
		for (String option : duplicate.keySet()) {
			if(duplicate.get(option) > 1) {
				System.out.println("duplicate option: " + option); 
				dup = true;
			}
		}
		return dup;

	}
}	

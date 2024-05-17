package emaillist;

import java.util.Scanner;

public class EmaillistApp {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		while (true) {
			System.out.print("(l)list (d)delete (i)insert (q)quit > ");
			String command = scanner.nextLine();
			
			if ("l".equals(command)) {
				doList();
			} else if ("d".equals(command)) {
				doDelete();
			} else if ("i".equals(command)) {
				doInsert();
			} else if ("q".equals(command)) {
				break;
			}
		}
		
		scanner.close();
	}
	
	private static void doList() {
		
	}
	
	private static void doDelete() {
		
	}

	private static void doInsert() {
		
	}

}

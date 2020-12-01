package userInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Statement;

import parser.Item;
import parser.Paser;
import api.KakaoAPI;
import database.Database;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Menu{
    
	static Tool function = new Tool();
	static Scanner in = new Scanner(System.in);
	public static ArrayList<Item> list = new ArrayList<>();
	public static ArrayList<Item> found = new ArrayList<>();
	public static Statement statement;
	public static void main(String[] args) throws Exception {
		//�Ŵ� 1�ϰ� 15�Ͽ� db������Ʈ
		SimpleDateFormat format = new SimpleDateFormat("dd");
		Calendar time = Calendar.getInstance();
		int currentDay =Integer.valueOf(format.format(time.getTime()));
		if(currentDay==1||currentDay==15) {
			//Paser.main(args);
		}
		
		//loadFromCSV();
		Database database = new Database();
		statement = Database.connect().createStatement();
		System.out.println(list.size());
		System.out.println("������ ����ǰ �˻� ���α׷��Դϴ�.");
		menu();
		
	}
	public static void menu() throws FileNotFoundException, SQLException {
		
		
		System.out.println("--------------�޴�--------------");
		System.out.println("1.��ǰ �˻�\t2.��ü ��ǰ ����Ʈ\t3.�ֺ� ������ ���\t4.���α׷� ����");
		try {
		int menuSelect = in.nextInt();
		switch(menuSelect) {
		case 1:
			System.out.println("��ǰ�� �˻��մϴ�.");
			searchItemMenu();
			break;
		case 2:
			showEntryList();
			break;
			
		case 3:
			System.out.println("�� m �ݰ� ���� �ִ� �������� Ȯ���Ͻðڽ��ϱ�? : ");
			int radius = in.nextInt();
			System.out.println(radius+"m ���� ������ ����� �����ݴϴ�.");
			KakaoAPI.find(radius);
			menu();
		case 4:
			System.out.println("���α׷��� �����մϴ�. ���� �Ϸ� �Ǽ���~");
			System.exit(0);
		default :
			menu();
			break;
				
		}
		}catch(InputMismatchException e) {
			menu();
		}
	}
	private static void loadFromCSV() throws FileNotFoundException {
		
		Path path = Paths.get("Type_All.csv");
		File file = new File(path.toUri());
		Scanner sc = new Scanner(file);
		sc.nextLine();
		while(sc.hasNextLine()) {
			String temp = sc.nextLine();
			String[] splited = temp.split(",");
			
			list.add(new Item(splited[0],Integer.valueOf(splited[1]),splited[2],splited[3]));
		}
		System.out.println("Database load complete!!");
		sc.close();
	}
	
	private static void showEntryList() throws FileNotFoundException, SQLException {
		//20���� �����ֱ�, 1~maxPage���� �������� ����Ʈ ����, 0�Է½� �޴���
	
		System.out.println("�� ��ǰ ���� :"+list.size());
		String brand= function.searchBrand();
			/*  
			 * KakaoAPI.find(radius); �ֺ� ������ ���̺� ����. 
			 * list = SQL.query(�ֺ� ������ ���̺� natural join ItemView ���̺�);
			 *
			 */
		String query;
		System.out.println("�˻� ���͸� �������ּ���.");
		System.out.println("1. �⺻ ���� 2. ���� �������� 3. ���� �������� 4. ��纰");
		if(brand.equals("all")) {
			query="Select pID, bName, pName, price, eName From Product Where;";
		}else {
			query="Select pID, bName, pName, price, eName From Product Where bName like concat('%','"+brand+"', '%');";
		}
		
		int filter=in.nextInt();
			switch(filter) {
			case 1:
				Menu.found = SQL.query(statement,query);
				break;
			case 2:
				list = SQL.SortByPrice(statement, query);
				break;
			case 3:
				list = SQL.SortByPriceDesc(statement, query);
				break;
			case 4:
				/*System.out.println("�� m �ݰ� ���� �ִ� �������� Ȯ���Ͻðڽ��ϱ�? : ");
				int radius = in.nextInt();
				System.out.println(radius+"m ���� ������ ����� �����ݴϴ�.");
				KakaoAPI.find(radius);
				System.out.println("�ش� ���������� �Ǹ��ϴ� ��ǰ ����Դϴ�.");
				//list = SQL.query(statement, "Select pID, bName, pName, price, eName From  ");
				 * */
				
				list = function.searchEvent("all","all");
				break;
			}
			
		int idx=0;
		int next=-1;
		for(;;) {
			if(next==-1) {
				System.out.println("-------------------------------------------��ǰ���-------------------------------------------------");
				System.out.println((next+2)+" ������/"+((list.size()/20)+1)+"������");
				System.out.println("----------------------------------------------------------------------------------------------------");
			}
			else {
				System.out.println();
				System.out.println("-------------------------------------------��ǰ���-------------------------------------------------");
				System.out.println((next)+" ������/"+((list.size()/20)+1)+"������");
				System.out.println("----------------------------------------------------------------------------------------------------");
			}
			System.out.printf("\t%-15s\t\t%-15s\t\t\t%s\t\t%s\n","������","��ǰ��","����","���");
			System.out.println("----------------------------------------------------------------------------------------------------");
			for(int i=idx;i<idx+20;i++) {
				try {
					System.out.printf("%-15s \t %25s \t\t\t %-8d \t%3s\n"
							,list.get(i).getBrand(),list.get(i).getName(),list.get(i).getPrice(),list.get(i).getEvent());
				}catch(IndexOutOfBoundsException e) {
					break;
				}
			}
			System.out.println("-----------------------------------------------------------------------------------------------------");
			System.out.print("(�޴� : 0) \t���ϴ� ������ : ");
			
			try {
				next = in.nextInt();
				
				if(next == 0 ) {
					System.out.println("�޴�ȭ������ ���ư��ϴ�.");
					menu();
				}else if(next > ((list.size()/20)+1)) {
					System.out.println("���� ������ �Դϴ�. ù �������� ���ư��ϴ�.");
					showEntryList();
				}
				else {
					idx=(next-1)*20;
					continue;
				}
			}catch(InputMismatchException e) {
				System.out.println("�޴�ȭ������ ���ư��ϴ�.");
				in = new Scanner(System.in);
				menu();
			}
		}
	}		
	
	
	private static void showList(String ItemName) throws FileNotFoundException, SQLException {
		//20���� �����ֱ�, 1~maxPage���� �������� ����Ʈ ����, 0�Է½� �޴���
	
		String brand= function.searchBrand();
		System.out.println("-----------------------------"+brand);
		ArrayList<Item> found = new ArrayList<>();
		found = Menu.found;
		System.out.println("�˻� ���͸� �������ּ���.");
		System.out.println("1.�⺻ ���� \t 2.���� �������� \t 3.���� �������� \t 4.��纰 \t5.�ֺ� ������");
		
		
		String query;
		if(brand.equals("all")) {
			query="Select pID, bName, pName, price, eName From "+ ItemName+"View Where;";
		}else {
			query="Select pID, bName, pName, price, eName From From "+ ItemName+"View  Where bName like concat('%','"+brand+"', '%');";
		}

		int filter=in.nextInt();
			switch(filter) {
			case 1:
				
				System.out.println(ItemName+brand);
				found = SQL.query(statement,query);
				break;
			case 2:
				found = SQL.SortByPrice(Menu.statement, query);
				break;
			case 3:
				found = SQL.SortByPriceDesc(Menu.statement, query);
				break;
			case 4:
				found = function.searchEvent(ItemName,brand);
				break;
			case 5:
				System.out.println("�� m �ݰ� ���� �ִ� �������� Ȯ���Ͻðڽ��ϱ�? : ");
				int radius = in.nextInt();
				System.out.println(radius+"m ���� ������ ����� �����ݴϴ�.");
				KakaoAPI.find(radius);
				System.out.println("�ش� ���������� �Ǹ��ϴ� ��ǰ ����Դϴ�.");
				//list = SQL.query(statement, "Select pID, bName, pName, price, eName From  ");
				break;
			default:
				System.out.println("�ٽ� �������ּ���.");
				showList(ItemName);
				break;
			}
		
		
		
		if(found.size()!=0) {
			in = new Scanner(System.in);
			System.out.println("�� ��ǰ ���� :"+found.size());

			
			int idx=0;
			int next=-1;
			for(;;) {
				if(next==-1) {
					System.out.println("-------------------------------------------��ǰ���-------------------------------------------------");
					System.out.println((next+2)+" ������/"+((found.size()/20)+1)+"������");
					System.out.println("----------------------------------------------------------------------------------------------------");
				}
				else {
					System.out.println();
					System.out.println("-------------------------------------------��ǰ���-------------------------------------------------");
					System.out.println((next)+" ������/"+((found.size()/20)+1)+"������");
					System.out.println("----------------------------------------------------------------------------------------------------");
				}
				System.out.printf("\t%-15s\t\t%-15s\t\t\t%s\t\t%s\n","������","��ǰ��","����","���");
				System.out.println("----------------------------------------------------------------------------------------------------");
				for(int i=idx;i<idx+20;i++) {
					try {
						System.out.printf("%-15s \t %25s \t\t\t %-8d \t%3s\n"
								,found.get(i).getBrand(),found.get(i).getName(),found.get(i).getPrice(),found.get(i).getEvent());
					}catch(IndexOutOfBoundsException e) {
						break;
					}
				}
				System.out.println("-----------------------------------------------------------------------------------------------------");
				System.out.print("(�޴� : 0) \t���ϴ� ������ : ");
				
				try {
					next = in.nextInt();
					if(next == 0 ) {
						//�� ���
						statement.executeUpdate("Drop view "+ItemName+"View Cascade;");
						
						System.out.println("�޴�ȭ������ ���ư��ϴ�.");
						menu();
					}else if(next > ((found.size()/20)+1)) {
						System.out.println("���� ������ �Դϴ�. ù �������� ���ư��ϴ�.");
						showList(ItemName);
					}
					else {
						idx=(next-1)*20;
						continue;
					}
				}catch(InputMismatchException e) {
					System.out.println("�޴�ȭ������ ���ư��ϴ�.");
					in = new Scanner(System.in);
					menu();
				}
			}
		}
		System.out.println("�޴�ȭ������ ���ư��ϴ�.");
		menu();
	}
	
	private static void searchItemMenu() throws SQLException {
		//�̸����� ��ǰ �˻�. ���ڿ� �����ϴ� ��� ��ǰ �����ֱ�. 0�� �Է��ϸ� �޴���
		in = new Scanner(System.in);
		System.out.println("�˻� ���͸� �������ּ���.");
		System.out.println("1. ��ǰ��\t\t0. ���� �޴�");
		try {
			int selectMenu = in.nextInt();
			switch(selectMenu) {
			case 1:
				String ItemName = function.searchName(list);
				showList(ItemName);
				break;
			case 0:
				System.out.println("���� �޴��� ���ư��ϴ�.");
				try {
					menu();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			default :
				System.out.println("�ٽ� �Է����ּ���.");
				searchItemMenu();
			}
		}catch(InputMismatchException e) {
			System.out.println("�Է��� �����Դϴ�. �ٽ� �õ����ּ���.");
			searchItemMenu();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}
}

class Tool extends ToolClass{
	Scanner in = new Scanner(System.in);
	@Override
	public String searchName(ArrayList<Item> list){
		String ItemName="";
	try {
			
		 do{ 
			System.out.println("ã����� ��ǰ���� �Է����ּ��� : ");
			ItemName =  in.next();
			if(ItemName.length()<2) {
				System.out.println("�� ���� �̻��� �Է����ּ���.");
				
			}
		}while(ItemName.length()<2);
		 
		 
		 Menu.statement.executeUpdate("create view " +ItemName+"View as Select pID, bName, pName, price, eName From Product Where pName like concat('%','"+ItemName+"','%');");
		 
		 Menu.found =SQL.query(Menu.statement,
			"Select pID, bName, pName, price, eName From Product Where pName like concat('%','"+ItemName+"', '%');");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ItemName;
	}

	@Override
	public ArrayList<Item> searchEvent(String ItemName,String brand) {
		System.out.println(brand);
		if(ItemName.equals("all")) {
			ItemName="Product";
		}else {
			ItemName=ItemName+"View";
		}
		while(true) {
			try{
				int toFind;
				Statement statement;
				String table;
				/*if(viewName.equals("all")) table = "Product";
				else table = viewName;*/
				String query="";
				System.out.println("���� �˻��մϴ�. ã����� ��縦 �������ּ���.");
				System.out.println("1. 1+1\t2. 2+1\t3. 3+1");
				toFind = in.nextInt();
				
				switch(toFind) {
				case 1:
					System.out.println("1+1��� ��ǰ�� �˻��մϴ�.");
					query = "Select pID, bName, pName, price, eName From "+ ItemName+" Where eName like concat('%','1+1','%')";
					break;
				
				case 2:
					System.out.println("2+1��� ��ǰ�� �˻��մϴ�.");
					query ="Select pID, bName, pName, price, eName From "+ ItemName+" Where eName like concat('%','2+1','%')";
					break;
				case 3:
					System.out.println("3+1��� ��ǰ�� �˻��մϴ�.");
					
					
					query = "Select pID, bName, pName, price, eName From "+ ItemName+" Where eName like concat('%','3+1','%')";
					break;
				default:
				
					System.out.println("�ٽ� �������ּ���.");
					break;
				}	
				
				
				
				if(brand.equals("all")) {
						Menu.found = SQL.query(Menu.statement, query);
					}else {
						Menu.found =SQL.query(Menu.statement,
								query+ " and bName = "+brand);
							
					}
				return Menu.found;
			}catch(InputMismatchException e) {
				
				System.out.println("�ٽ� �������ּ���.");
			}catch (SQLException e) {
					e.printStackTrace();
				}
		}	
		
	}

	@Override
	public String searchBrand() {
		int toFind=-1;
		while(toFind==-1) {
			System.out.println("�������� �������ּ���.(1.�̴Ͻ���\t2.GS25\t3.CU\t4.�̸�Ʈ24\t5.�����Ϸ���)\n��� �������� ��ǰ�� ���÷��� '0'�� �Է����ּ���.");
			try{
				toFind =  in.nextInt();
			}catch(InputMismatchException e) {
				System.out.println("1~5�� �Է����ּ���.");
				searchBrand();
			}
			switch(toFind) {
				case 1:
					/*
					 *Qeury�̿��� ã�� 
					 */
					System.out.println("�̴Ͻ��鿡�� �˻��մϴ�.");
					return "�̴Ͻ���";
				case 2:
					/*
					 *Qeury�̿��� ã�� 
					 */
					System.out.println("GS25���� �˻��մϴ�.");
					return "GS25";
				case 3:
					/*
					 *Qeury�̿��� ã�� 
					 */
					System.out.println("CU���� �˻��մϴ�.");
					return "CU";
				case 4:
					/*
					 *Qeury�̿��� ã�� 
					 */
					System.out.println("�̸�Ʈ24���� �˻��մϴ�.");
					return "�̸�Ʈ24";
				case 5:
					/*
					 *Qeury�̿��� ã�� 
					 */
					System.out.println("�����Ϸ��쿡�� �˻��մϴ�.");
					return "�����Ϸ���";
				case 0:
					System.out.println("��ü ������ ��Ͽ��� �˻��մϴ�.");
					return "all";
				default:
					toFind=-1;
					System.out.println("�ٽ� �������ּ���.");
					break;
			}
		}
		return "all";
	}

	@Override
	public ArrayList<Item> searchClosest(ArrayList<Item> list) {
		ArrayList<Item> found = new ArrayList<>();
		int radius=1000;
		System.out.println("�� ���� �̳��� �ִ� �������� ã���ðڽ��ϱ�?(�⺻ : 1000m) : ");
		radius = in.nextInt();
		try {
			KakaoAPI.find(radius);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return found;
	}


}



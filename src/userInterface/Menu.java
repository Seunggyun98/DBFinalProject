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
		//매달 1일과 15일에 db업데이트
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
		System.out.println("편의점 행사상품 검색 프로그램입니다.");
		menu();
		
	}
	public static void menu() throws FileNotFoundException, SQLException {
		
		
		System.out.println("--------------메뉴--------------");
		System.out.println("1.상품 검색\t2.전체 상품 리스트\t3.주변 편의점 목록\t4.프로그램 종료");
		try {
		int menuSelect = in.nextInt();
		switch(menuSelect) {
		case 1:
			System.out.println("상품을 검색합니다.");
			searchItemMenu();
			break;
		case 2:
			showEntryList();
			break;
			
		case 3:
			System.out.println("몇 m 반경 내에 있는 편의점을 확인하시겠습니까? : ");
			int radius = in.nextInt();
			System.out.println(radius+"m 내의 편의점 목록을 보여줍니다.");
			KakaoAPI.find(radius);
			menu();
		case 4:
			System.out.println("프로그램을 종료합니다. 좋은 하루 되세요~");
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
		//20개씩 보여주기, 1~maxPage까지 선택으로 리스트 갱신, 0입력시 메뉴로
	
		System.out.println("총 상품 개수 :"+list.size());
		String brand= function.searchBrand();
			/*  
			 * KakaoAPI.find(radius); 주변 편의점 테이블 저장. 
			 * list = SQL.query(주변 편의점 테이블 natural join ItemView 테이블);
			 *
			 */
		String query;
		System.out.println("검색 필터를 설정해주세요.");
		System.out.println("1. 기본 정렬 2. 가격 오름차순 3. 가격 내림차순 4. 행사별");
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
				/*System.out.println("몇 m 반경 내에 있는 편의점을 확인하시겠습니까? : ");
				int radius = in.nextInt();
				System.out.println(radius+"m 내의 편의점 목록을 보여줍니다.");
				KakaoAPI.find(radius);
				System.out.println("해당 편의점에서 판매하는 상품 목록입니다.");
				//list = SQL.query(statement, "Select pID, bName, pName, price, eName From  ");
				 * */
				
				list = function.searchEvent("all","all");
				break;
			}
			
		int idx=0;
		int next=-1;
		for(;;) {
			if(next==-1) {
				System.out.println("-------------------------------------------상품목록-------------------------------------------------");
				System.out.println((next+2)+" 페이지/"+((list.size()/20)+1)+"페이지");
				System.out.println("----------------------------------------------------------------------------------------------------");
			}
			else {
				System.out.println();
				System.out.println("-------------------------------------------상품목록-------------------------------------------------");
				System.out.println((next)+" 페이지/"+((list.size()/20)+1)+"페이지");
				System.out.println("----------------------------------------------------------------------------------------------------");
			}
			System.out.printf("\t%-15s\t\t%-15s\t\t\t%s\t\t%s\n","편의점","상품명","가격","행사");
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
			System.out.print("(메뉴 : 0) \t원하는 페이지 : ");
			
			try {
				next = in.nextInt();
				
				if(next == 0 ) {
					System.out.println("메뉴화면으로 돌아갑니다.");
					menu();
				}else if(next > ((list.size()/20)+1)) {
					System.out.println("없는 페이지 입니다. 첫 페이지로 돌아갑니다.");
					showEntryList();
				}
				else {
					idx=(next-1)*20;
					continue;
				}
			}catch(InputMismatchException e) {
				System.out.println("메뉴화면으로 돌아갑니다.");
				in = new Scanner(System.in);
				menu();
			}
		}
	}		
	
	
	private static void showList(String ItemName) throws FileNotFoundException, SQLException {
		//20개씩 보여주기, 1~maxPage까지 선택으로 리스트 갱신, 0입력시 메뉴로
	
		String brand= function.searchBrand();
		System.out.println("-----------------------------"+brand);
		ArrayList<Item> found = new ArrayList<>();
		found = Menu.found;
		System.out.println("검색 필터를 설정해주세요.");
		System.out.println("1.기본 정렬 \t 2.가격 오름차순 \t 3.가격 내림차순 \t 4.행사별 \t5.주변 편의점");
		
		
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
				System.out.println("몇 m 반경 내에 있는 편의점을 확인하시겠습니까? : ");
				int radius = in.nextInt();
				System.out.println(radius+"m 내의 편의점 목록을 보여줍니다.");
				KakaoAPI.find(radius);
				System.out.println("해당 편의점에서 판매하는 상품 목록입니다.");
				//list = SQL.query(statement, "Select pID, bName, pName, price, eName From  ");
				break;
			default:
				System.out.println("다시 선택해주세요.");
				showList(ItemName);
				break;
			}
		
		
		
		if(found.size()!=0) {
			in = new Scanner(System.in);
			System.out.println("총 상품 개수 :"+found.size());

			
			int idx=0;
			int next=-1;
			for(;;) {
				if(next==-1) {
					System.out.println("-------------------------------------------상품목록-------------------------------------------------");
					System.out.println((next+2)+" 페이지/"+((found.size()/20)+1)+"페이지");
					System.out.println("----------------------------------------------------------------------------------------------------");
				}
				else {
					System.out.println();
					System.out.println("-------------------------------------------상품목록-------------------------------------------------");
					System.out.println((next)+" 페이지/"+((found.size()/20)+1)+"페이지");
					System.out.println("----------------------------------------------------------------------------------------------------");
				}
				System.out.printf("\t%-15s\t\t%-15s\t\t\t%s\t\t%s\n","편의점","상품명","가격","행사");
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
				System.out.print("(메뉴 : 0) \t원하는 페이지 : ");
				
				try {
					next = in.nextInt();
					if(next == 0 ) {
						//뷰 드랍
						statement.executeUpdate("Drop view "+ItemName+"View Cascade;");
						
						System.out.println("메뉴화면으로 돌아갑니다.");
						menu();
					}else if(next > ((found.size()/20)+1)) {
						System.out.println("없는 페이지 입니다. 첫 페이지로 돌아갑니다.");
						showList(ItemName);
					}
					else {
						idx=(next-1)*20;
						continue;
					}
				}catch(InputMismatchException e) {
					System.out.println("메뉴화면으로 돌아갑니다.");
					in = new Scanner(System.in);
					menu();
				}
			}
		}
		System.out.println("메뉴화면으로 돌아갑니다.");
		menu();
	}
	
	private static void searchItemMenu() throws SQLException {
		//이름으로 상품 검색. 문자열 포함하는 모든 상품 보여주기. 0을 입력하면 메뉴로
		in = new Scanner(System.in);
		System.out.println("검색 필터를 설정해주세요.");
		System.out.println("1. 상품명\t\t0. 메인 메뉴");
		try {
			int selectMenu = in.nextInt();
			switch(selectMenu) {
			case 1:
				String ItemName = function.searchName(list);
				showList(ItemName);
				break;
			case 0:
				System.out.println("메인 메뉴로 돌아갑니다.");
				try {
					menu();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			default :
				System.out.println("다시 입력해주세요.");
				searchItemMenu();
			}
		}catch(InputMismatchException e) {
			System.out.println("입력은 숫자입니다. 다시 시도해주세요.");
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
			System.out.println("찾고싶은 상품명을 입력해주세요 : ");
			ItemName =  in.next();
			if(ItemName.length()<2) {
				System.out.println("두 글자 이상을 입력해주세요.");
				
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
				System.out.println("행사로 검색합니다. 찾고싶은 행사를 선택해주세요.");
				System.out.println("1. 1+1\t2. 2+1\t3. 3+1");
				toFind = in.nextInt();
				
				switch(toFind) {
				case 1:
					System.out.println("1+1행사 상품을 검색합니다.");
					query = "Select pID, bName, pName, price, eName From "+ ItemName+" Where eName like concat('%','1+1','%')";
					break;
				
				case 2:
					System.out.println("2+1행사 상품을 검색합니다.");
					query ="Select pID, bName, pName, price, eName From "+ ItemName+" Where eName like concat('%','2+1','%')";
					break;
				case 3:
					System.out.println("3+1행사 상품을 검색합니다.");
					
					
					query = "Select pID, bName, pName, price, eName From "+ ItemName+" Where eName like concat('%','3+1','%')";
					break;
				default:
				
					System.out.println("다시 선택해주세요.");
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
				
				System.out.println("다시 선택해주세요.");
			}catch (SQLException e) {
					e.printStackTrace();
				}
		}	
		
	}

	@Override
	public String searchBrand() {
		int toFind=-1;
		while(toFind==-1) {
			System.out.println("편의점을 선택해주세요.(1.미니스톱\t2.GS25\t3.CU\t4.이마트24\t5.세븐일레븐)\n모든 편의점의 상품을 보시려면 '0'을 입력해주세요.");
			try{
				toFind =  in.nextInt();
			}catch(InputMismatchException e) {
				System.out.println("1~5로 입력해주세요.");
				searchBrand();
			}
			switch(toFind) {
				case 1:
					/*
					 *Qeury이용해 찾기 
					 */
					System.out.println("미니스톱에서 검색합니다.");
					return "미니스톱";
				case 2:
					/*
					 *Qeury이용해 찾기 
					 */
					System.out.println("GS25에서 검색합니다.");
					return "GS25";
				case 3:
					/*
					 *Qeury이용해 찾기 
					 */
					System.out.println("CU에서 검색합니다.");
					return "CU";
				case 4:
					/*
					 *Qeury이용해 찾기 
					 */
					System.out.println("이마트24에서 검색합니다.");
					return "이마트24";
				case 5:
					/*
					 *Qeury이용해 찾기 
					 */
					System.out.println("세븐일레븐에서 검색합니다.");
					return "세븐일레븐";
				case 0:
					System.out.println("전체 편의점 목록에서 검색합니다.");
					return "all";
				default:
					toFind=-1;
					System.out.println("다시 선택해주세요.");
					break;
			}
		}
		return "all";
	}

	@Override
	public ArrayList<Item> searchClosest(ArrayList<Item> list) {
		ArrayList<Item> found = new ArrayList<>();
		int radius=1000;
		System.out.println("몇 미터 이내에 있는 편의점을 찾으시겠습니까?(기본 : 1000m) : ");
		radius = in.nextInt();
		try {
			KakaoAPI.find(radius);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return found;
	}


}



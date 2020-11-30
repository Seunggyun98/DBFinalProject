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
	static ArrayList<Item> list = new ArrayList<>();
	public static void main(String[] args) throws Exception {
		//매달 1일과 15일에 db업데이트
		SimpleDateFormat format = new SimpleDateFormat("dd");
		Calendar time = Calendar.getInstance();
		int currentDay =Integer.valueOf(format.format(time.getTime()));
		if(currentDay==1||currentDay==15) {
			Paser.main(args);
		}
		
		loadFromCSV();
		Database database = new Database();
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
		System.out.println("검색 필터를 설정해주세요.");
		System.out.println("1. 기본 정렬 2. 가격 오름차순 3. 가격 내림차순 4. 행사별");
		System.out.println("------------전체 상품 목록------------");
		int idx=0;
		int next=-1;
		for(;;) {
			if(next==-1) {
				System.out.println((next+2)+" 페이지/"+((list.size()/20)+1)+"페이지");
			}else {
				System.out.println((next)+" 페이지/"+((list.size()/20)+1)+"페이지");
			}
			System.out.printf("\t%-15s\t\t%15s\t\t\t\t%s\t\t       %s\n","편의점","상품명","가격","행사");
			
			for(int i=idx;i<idx+20;i++) {
				try {
					System.out.printf("\t%-15s\t\t%-40s\t    %-20s%s\n"
							,list.get(i).getBrand(),list.get(i).getName(),list.get(i).getPrice(),list.get(i).getEvent());
				}catch(IndexOutOfBoundsException e) {
					break;
				}
			}
			System.out.println("(메뉴 : 0) \t원하는 페이지 : ");
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
	
	private static void showList(ArrayList<Item> list) throws FileNotFoundException, SQLException {
		//20개씩 보여주기, 1~maxPage까지 선택으로 리스트 갱신, 0입력시 메뉴로
	
		Statement statement = Database.connect().createStatement();
		if(list.size()!=0) {
			in = new Scanner(System.in);
			System.out.println("총 상품 개수 :"+list.size());
			
			
			System.out.println("--------------상품 목록--------------");
			int idx=0;
			int next=-1;
			for(;;) {
				if(next==-1) {
					System.out.println((next+2)+" 페이지/"+((list.size()/20)+1)+"페이지");
				}else {
					System.out.println((next)+" 페이지/"+((list.size()/20)+1)+"페이지");
				}
				System.out.printf("\t%-15s\t\t%15s\t\t\t\t%s\t\t       %s\n","편의점","상품명","가격","행사");
				
				for(int i=idx;i<idx+20;i++) {
					try {
						System.out.printf("\t%-15s\t\t%-40s\t    %-20s%s\n"
								,list.get(i).getBrand(),list.get(i).getName(),list.get(i).getPrice(),list.get(i).getEvent());
					}catch(IndexOutOfBoundsException e) {
						break;
					}
				}
				
				System.out.println("(메뉴 : 0) \t원하는 페이지 : ");
				try {
					next = in.nextInt();
					if(next == 0 ) {
						System.out.println("메뉴화면으로 돌아갑니다.");
						menu();
					}else if(next > ((list.size()/20)+1)) {
						System.out.println("없는 페이지 입니다. 첫 페이지로 돌아갑니다.");
						showList(list);
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
		ArrayList<Item> found = new ArrayList<>();
		System.out.println("검색 필터를 설정해주세요.");
		System.out.println("1. 상품명\t\t2. 행사\t\t0. 메인 메뉴");
		try {
			int selectMenu = in.nextInt();
			switch(selectMenu) {
			case 1:
				found = function.searchName(list);
				showList(found);
				break;
			case 2:
				found = function.searchEvent(list);
				showList(found);
				break;
			case 0:
				System.out.println("메인 메뉴로 돌아갑니다.");
				try {
					menu();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	public ArrayList<Item> searchName(ArrayList<Item> list){
		ArrayList<Item> found = new ArrayList<>();
		String ItemName="";
	try {
			Statement statement = Database.connect().createStatement();
		 do{ 
			System.out.println("찾고싶은 상품명을 입력해주세요 : ");
			ItemName =  in.next();
			if(ItemName.length()<2) {
				System.out.println("두 글자 이상을 입력해주세요.");
				
			}
		}while(ItemName.length()<2);
		 String brand= searchBrand();
		 
		 System.out.println("검색 필터를 설정해주세요.");
			System.out.println("1. 기본 정렬\t2. 가격 오름차순\t3. 가격 내림차순\t4. 주변 편의점");
			/*  
			 * KakaoAPI.find(radius); 주변 편의점 테이블 저장. 
			 * list = SQL.query(주변 편의점 테이블 natural join ItemView 테이블);
			 *
			 */
			
			int filter=in.nextInt();
			switch(filter) {
			case 1:
				break;
			case 2:
				list = SQL.SortByPrice(statement, ItemName, brand);
				return list;
	
			case 3:
				list = SQL.SortByPriceDesc(statement, ItemName, brand);
				return list;
			case 4:
				System.out.println("몇 m 반경 내에 있는 편의점을 확인하시겠습니까? : ");
				int radius = in.nextInt();
				System.out.println(radius+"m 내의 편의점 목록을 보여줍니다.");
				KakaoAPI.find(radius);
				System.out.println("해당 편의점에서 판매하는 상품 목록입니다.");
				//list = SQL.query(statement, "Select pID, bName, pName, price, eName From  ");
				break;
			}
		 
		 
		 
		//sql문으로 검색 후 found에 저장
		
		
			if(brand != "all") {
				found = SQL.query(statement,
						"Select pID, bName, pName, price, eName From Product Where pName like concat('%','"+ItemName+"', '%') and bName like concat('%','"+brand+"','%');");
				
			}else {
				found =SQL.query(statement,
						"Create view ItemView as Select pID, bName, pName, price, eName From Product Where pName like concat('%','"+ItemName+"', '%');");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return found;
	}

	@Override
	public ArrayList<Item> searchEvent(ArrayList<Item> list) {
		ArrayList<Item> found = new ArrayList<>();
		int toFind=0;
		while(toFind==0) {
			try{
				Statement statement;
				String brand;
				statement = Database.connect().createStatement();
				System.out.println("행사로 검색합니다. 찾고싶은 행사를 선택해주세요.");
				System.out.println("1. 1+1\t2. 2+1\t3. 3+1");
				toFind = in.nextInt();
				switch(toFind) {
				case 1:
					System.out.println("1+1행사 상품을 검색합니다.");
					brand = searchBrand();
					found=SQL.query(statement,"Select pID, bName, pName, price, eName From Product Where eName = '1+1' and bName like concat('%','"+brand+"','%');");
					break;
				case 2:
					System.out.println("2+1행사 상품을 검색합니다.");
					brand = searchBrand();
					found=SQL.query(statement,"Select pID, bName, pName, price, eName From Product Where eName = '2+1' and bName like concat('%','"+brand+"','%');");
					toFind=2;
					break;
				case 3:
					System.out.println("3+1행사 상품을 검색합니다.");
					brand = searchBrand();
					found=SQL.query(statement,"Select pID, bName, pName, price, eName From Product Where eName = '3+1' and bName like concat('%','"+brand+"','%');");
					break;
				default:
					toFind=0;
					System.out.println("다시 선택해주세요.");
					break;
				}
			}catch(InputMismatchException e) {
				toFind=0;
				System.out.println("다시 선택해주세요.");
			}catch (SQLException e) {
					e.printStackTrace();
				}
		}	
		return found;
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

	@Override
	public ArrayList<Item> sortPrice(ArrayList<Item> list) {
		ArrayList<Item> found = new ArrayList<>();
		
		
		return found;
	}
}



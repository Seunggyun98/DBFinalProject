package userInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import parser.Item;

interface ToolInterface {
	public String searchName(ArrayList<Item> list);
	public ArrayList<Item> searchEvent(String ItemName,String brand);
	public String searchBrand();
	public ArrayList<Item> searchClosest(ArrayList<Item> list);
	public ArrayList<Item> sortPrice(ArrayList<Item> list);
}

class ToolClass implements ToolInterface{

	@Override
	public String searchName(ArrayList<Item> list){
		return null;
	}

	@Override
	public ArrayList<Item> searchEvent(String ItemName,String brand) {
		return null;
	}

	@Override
	public String searchBrand() {
		return null;
	}

	@Override
	public ArrayList<Item> searchClosest(ArrayList<Item> list) {
		return null;
	}

	@Override
	public ArrayList<Item> sortPrice(ArrayList<Item> list) {
		return null;
	}
	
}

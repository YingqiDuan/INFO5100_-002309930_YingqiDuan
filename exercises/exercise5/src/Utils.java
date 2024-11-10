import FactoryMethodPattern.MenuItem;

import java.util.List;
public class Utils {
    public static void displayMenu(List<MenuItem> menuItems){
        System.out.println("\n--- Menu ---");
        for(int i=0;i<menuItems.size();i++){
            System.out.print((i+1)+". ");
            menuItems.get(i).display();
        }
    }
}

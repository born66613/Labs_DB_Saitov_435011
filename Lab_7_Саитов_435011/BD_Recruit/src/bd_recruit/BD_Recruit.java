/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bd_recruit;
import java.sql.*;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Vector;
import javax.print.DocFlavor;

/**
 *
 * @author Саитов Илья
 */
public class BD_Recruit {
    public static void main(String[] args) throws Exception {
        // Объявляем переменные:
        String temp2 = null;                            // Временные строковые 
        String [] temp3 = null;                         //строковые переменные
        Vector<String> vec_tab = new Vector<String>();  // Вектор для хранения списка таблиц
        ResultSet rs_data;                              // "Курсор" для указания текущей строки
        int numb_command = 0;                           // Номер введенной команды
        int numb_table = 0;                             // Номер введенной таблицы
        int t1, t2, t3, t4, t5, t6;                     // Временные целые для аргументов процедуры
        int cnt_col = 0;                                // Количество столбцов в таблице
        // Указываем путь к БД, записывая его в переменную:
        //String strURL = "jdbc:firebirdsql://localhost/G:\\\\RECRUIT.fdb?lc_ctype=WIN1251&charSet=Cp1251";
        String strURL = "jdbc:firebirdsql://localhost/G:\\\\RECRUIT.fdb";
        // Инициализация Firebird JDBC driver:
        Class.forName("org.firebirdsql.jdbc.FBDriver").newInstance();
        PrintStream printStream = new PrintStream(System.out, true, "cp866");
	Scanner sc = new Scanner(System.in, "cp866");
        //Создание подключение к БД:
        Connection connect=null;
	connect = DriverManager.getConnection(strURL,"SYSDBA", "masterkey");
        if (connect==null)
	{
            printStream.println("Подключение к БД завершилось ошибкой");
	}
        // Получаем список всех таблиц БД:
        DatabaseMetaData metaData = connect.getMetaData();
        ResultSet temp=metaData.getTables(temp2, temp2, temp2, temp3);
        while(temp.next())
	{
            temp2=temp.getString(3);
            if(!temp2.contains("$"))
                vec_tab.add(temp2);
        }
        // Создаём класс, с помощью которого будут выполняться SQL запросы:
        Statement stmt = connect.createStatement();
        while(numb_command!=4)
        {
            printStream.println();
            printStream.println("Нажмите на соответствующую клавишу:");
            printStream.println("1. Вывести список таблиц");
            printStream.println("2. Добавить ФИО в список врачей");
            printStream.println("3. Выполнить ХП по созданию призыва");
            printStream.println("4. Заверишить сеанс");
            try{
                numb_command=Integer.parseInt(sc.nextLine());
            }catch(NumberFormatException e){
                printStream.println("Нельзя вводить посторонние символы!");
                continue;
            }
            if(numb_command>4){
                printStream.println("Команда с таким номером отсутствует!");
            }    
            // Если нажали на 1, то выполняем команду 1:
            if(numb_command==1)
            {   
                printStream.println("Список таблиц базы данных военкомата:");
                for(int i=1;i<=vec_tab.size();i++)
                {
                    System.out.printf("%d. %s\n",i,vec_tab.elementAt(i-1));
                }
                printStream.println("Введите номер таблицы для отображения ее содержимого:");
                try{
                    numb_table=Integer.parseInt(sc.nextLine());
                }catch(NumberFormatException e){
                    printStream.println("Нельзя вводить посторонние символы!");
                    continue;
                }
                if(numb_table > vec_tab.size()){
                    printStream.println("Введено слишком большое число!");
                    continue;
                }
                printStream.println();
                //Выполняем SQL запрос:
          	rs_data = stmt.executeQuery("SELECT * from "+ vec_tab.elementAt(numb_table-1));
                // Выводим результат:
                cnt_col = rs_data.getMetaData().getColumnCount();
                // Выводим содержание таблицы:
                // Сначала имена столбцов:
                for(int i = 1; i < cnt_col + 1; i++){
                    printStream.print(rs_data.getMetaData().getColumnName(i)+
                            "  |  ");
                }
                // Затем сами записи в таблице:
                while(rs_data.next())
                {
                    printStream.println();
                    for (int i = 1;i < cnt_col + 1;i++)
                    {
                            Object obj = rs_data.getObject(i);
                            if (obj!=null)
                            {
                                    printStream.print(obj+"   \t   ");
                            }
                    }
                }
                printStream.println();
                continue;
            }
            // Если нажали на 2, то выполняем команду 2:
            if(numb_command==2)
            { 
                printStream.println("Введите ФИО врача");
                String tstr=sc.nextLine();
                if(tstr.length()>64)
                {
                    printStream.println("ФИО превысило допустимую длину в 64 символа!");
                    continue;
                }
                tstr="INSERT INTO listdoctor(FULLNAME) VALUES ('"+tstr+"')";
                stmt.executeUpdate(tstr);
                continue;
            }
            // Если нажали на 3, то выполняем команду 3:
            if(numb_command==3)
            { 
               try{
                    printStream.println("Введите количество людей, подлежащих призыву:");
                    t1=Integer.parseInt(sc.nextLine());
                    printStream.println("Введите идентификатор звания:");
                    t2=Integer.parseInt(sc.nextLine());
                    printStream.println("Введите идентификатор должности:");
                    t3=Integer.parseInt(sc.nextLine());
                    printStream.println("Введите идентификатор специальности:");
                    t4=Integer.parseInt(sc.nextLine());
                    printStream.println("Введите идентификатор военной части:");
                    t5=Integer.parseInt(sc.nextLine());
                    printStream.println("Введите срок службы призывников:");
                    t6=Integer.parseInt(sc.nextLine());
                    // Вызов хранимой процедуры без возвращаемых параметров:
                    PreparedStatement call_stmt=connect.prepareStatement("{call rec_appeal(?,?,?,?,?,?)}");
                    call_stmt.setInt(1, t1);
                    call_stmt.setInt(2, t2);
                    call_stmt.setInt(3, t3);
                    call_stmt.setInt(4, t4);
                    call_stmt.setInt(5, t5);
                    call_stmt.setInt(6, t6);
                    call_stmt.execute();
                    printStream.println();
                    printStream.println("Было успешно призвано "+t1+" человек сроком на "+t6+" дней");
                    continue;
                }catch(NumberFormatException e){
                    printStream.println("Вводить можно только числа!");
                    continue;
                }
            }     
        }
        // Если нажали 4, то освобождаем ресурсы и завершаем работу.
	stmt.close();
	connect.close();
    }
}

package com.studentExpense;

import java.io.*;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Test {

    private static final String DIRECTORY_NAME ="c:\\StudExpenseFiles";
    private static final String BILL_DIRECTORY_NAME="c:\\StudExpenseFiles\\bill";
    private static final String ITEM_DIRECTORY_NAME ="c:\\StudExpenseFiles\\checkOut";
    private static final String SIGNUP_FILE_NAME = "signUpInfo.txt";
    public static String usernameCache = "";

    public static void main(String args[])throws IOException{
        displayMainMenu();
        Scanner sc = new Scanner(System.in);
        selectedMainMenu(sc);

    }

    public static void displayMainMenu(){
        StringBuffer menu = new StringBuffer();
        menu.append("**********************************************\n");
        menu.append("\t\tSTUDENT EXPENSE SYSTEM\n");
        menu.append("**********************************************\n");
        menu.append("\t\t[1]\tSign up\n");
        menu.append("\t\t[2]\tLogin\n");
        menu.append("\t\t[3]\tCheckOut\n");
        menu.append("\t\t[4]\tExit\n");
        menu.append("**********************************************\n");
        menu.append("Please choose the number from menu!\n");
        System.out.print(menu.toString());

    }
    public static void selectedMainMenu(Scanner sc)throws IOException {
        int s = sc.nextInt();
        String pathNames[] = null;
        switch(s){

            case 1:
            signUp(sc);
            displayMainMenu();
            selectedMainMenu(sc);
                break;

            case 2:
                signIn(sc);
                displayMainMenu();
                selectedMainMenu(sc);
                break;

           case 3:
               File dir =new File(ITEM_DIRECTORY_NAME);

               if(dir.exists()){
                   pathNames = dir.list();

                   if(pathNames != null) {
                       for (String pathName : pathNames) {
                           System.out.println(pathName+" :Your Bills are Given Below: \n");
                           System.out.println("**********************************************\n");
                           viewCheckoutOrBill(null, ITEM_DIRECTORY_NAME + "\\" + pathName);
                       }
                   }
               }

            case 4:
                System.out.println("Exited!");
                break;
                default:
                    System.out.println("Please Enter a Vaid menu Number!");
                    displayMainMenu();
                    selectedMainMenu(sc);

        }
    }

    public static void signUp(Scanner sc)throws IOException{
    String firstName = null;
    String lastName = null;
    String userName = null;
    String password = null;
    StringBuffer signUpMessage = new StringBuffer();

    System.out.println("**********************************************\n");
    System.out.println("Register the User!\n");
    System.out.println("**********************************************\n");
    System.out.println("Please Enter the First Name:\t");
    firstName =sc.next();
    System.out.println("\nPlease Enter the Last Name:\t");
    lastName=sc.next();
    System.out.println("\nPlease Enter the UserName:\t");
    userName = sc.next();
    System.out.println("\nPlease Enter the password:\t");
    password = sc.next();

    WriteSignUpInfoIntoFile(firstName,lastName,userName,password);
    }

    public static void WriteSignUpInfoIntoFile(String firstName,String lastName,String userName,String password)throws  IOException{
        File dir = new File(DIRECTORY_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(DIRECTORY_NAME+"\\"+SIGNUP_FILE_NAME);
        FileWriter fw = null;
        BufferedWriter bw = null; PrintWriter pw = null;

            try {
                if (file.exists()) {
                    fw = new FileWriter(DIRECTORY_NAME+"\\"+SIGNUP_FILE_NAME, true);

                    bw = new BufferedWriter(fw);
                    pw = new PrintWriter(bw);
                    pw.println("\n");
                    pw.println(firstName + "," + lastName + "," + userName + "," + encryptPassword(password)+"\n");

                    pw.flush();
                }else{
                    fw = new FileWriter(DIRECTORY_NAME+"\\"+SIGNUP_FILE_NAME);
                    bw = new BufferedWriter(fw);
                    bw.write(firstName+","+lastName+","+userName+","+encryptPassword(password)+"\n");
                }
                System.out.println("Successfully Registered!");
            }
            finally {
                    if(pw != null) {
                        pw.close();
                    }
                    bw.close();
                    fw.close();
            }
        }

    public static void signIn(Scanner sc)throws IOException{
        String userName = null;
        String password = null;
        StringBuffer loginMessage = new StringBuffer();

        System.out.println("**********************************************\n");
        System.out.println("Login!\n");
        System.out.println("**********************************************\n");
        System.out.println("Please Enter the UserName:\t");
        userName =sc.next();
        System.out.println("\nPlease Enter the Password:\t");
        password=sc.next();
        boolean validationFailed = validateSignInfo(userName,encryptPassword(password));

        if(!validationFailed){
            System.out.println("**********************************************\n");
            System.out.println("\tWelcome"+userName+"\t");
            System.out.println("**********************************************\n");
            displaySubMenu();
            selectedSubMenu(sc);
        }

    }

    public static void selectedSubMenu(Scanner sc)throws IOException {
        int s = sc.nextInt();
        String pathNames[] =null;
        switch(s){

            case 1:
                addItem(sc);
                displaySubMenu();
                selectedSubMenu(sc);
                break;

            case 2:
               deleteItem(sc);
                displaySubMenu();
                selectedSubMenu(sc);
                break;

            case 3:

                viewCheckoutOrBill(null,BILL_DIRECTORY_NAME+"\\"+usernameCache);
                displaySubMenu();
                selectedSubMenu(sc);
                break;

            case 4:
                File dir =new File(BILL_DIRECTORY_NAME);

                if(dir.exists()){
                    pathNames = dir.list();

                    if(pathNames != null) {
                        for (String pathName : pathNames) {
                            System.out.println(pathName+":\n");
                            System.out.println("**********************************************\n");
                            viewCheckoutOrBill(null, BILL_DIRECTORY_NAME + "\\" + pathName);
                        }
                    }
                }

                displaySubMenu();
                selectedSubMenu(sc);
                break;
            case 5:
                Float sumOfItem =viewCheckoutOrBill("Total",ITEM_DIRECTORY_NAME+"\\"+usernameCache);
                if(sumOfItem != null) {
                    System.out.println("\t\tPlease Enter Yes For CheckOut or No to return to Menu!");
                    String menuName=sc.next();
                    if(menuName != null && menuName.equalsIgnoreCase("yes")) {
                        addBill(sumOfItem);
                    }
                }else{
                    System.out.println("No Items Found!");
                }
                displaySubMenu();
                selectedSubMenu(sc);
                break;

            case 6:
                displayMainMenu();
                selectedMainMenu(sc);
                break;

            default:
                System.out.println("Please Enter a Vaid menu Number!");
                displaySubMenu();
                selectedSubMenu(sc);
                break;

        }
    }
    public static void addItem(Scanner sc)throws IOException{
        String itemName = null;
        Float price = null;
        Integer quantity = null;

        System.out.println("**********************************************\n");
        System.out.println("Add The Item !\n");
        System.out.println("**********************************************\n");
        System.out.println("Please Enter the Item Name :\t");
        try {
            itemName = sc.next();
            System.out.println("Please Enter the Price:\t");
            price = sc.nextFloat();
            System.out.println("\nPlease Enter the Quantity:\t");
            quantity = sc.nextInt();
            WriteItemIntoFile(itemName, price, quantity);
        }catch(InputMismatchException e){
            System.out.println("Enter Valid Input!");
        }

    }

    public static void addBill(Float total)throws IOException{
        File dir = new File(BILL_DIRECTORY_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File destination  = new File(BILL_DIRECTORY_NAME+"\\"+usernameCache);
        File source = new File(ITEM_DIRECTORY_NAME+"\\"+usernameCache);
        FileWriter fw = null;
        BufferedWriter bw = null; PrintWriter pw = null;
        BufferedReader reader = null;
        try {
            if (destination.exists()) {

                reader = new BufferedReader(new FileReader(source));
                String line = null;
                fw = new FileWriter(destination, true);
                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                //pw.println("\n");
                while((line = reader.readLine()) != null){
                pw.println(line+"\n");
                }

            }else{
                copyFile(source,destination);
                fw = new FileWriter(destination, true);
                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
            }
            source.delete();
            pw.println("************"+"\n");
            pw.println("Total: "+total);
            pw.println("************"+"\n");
            pw.flush();
            System.out.println("Paid Successfully!");
        }
        finally {
            if(pw != null) {
                pw.close();
            }
            if(reader != null){
                reader.close();
            }
            bw.close();
            fw.close();
        }
    }

    public static void copyFile(File source,File destination) throws IOException{
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }

    public static void deleteItem(Scanner sc)throws IOException{
        System.out.println("**********************************************\n");
        System.out.println("Delete Item!\n");
        System.out.println("**********************************************\n");

        viewCheckoutOrBill(null,ITEM_DIRECTORY_NAME+"\\"+usernameCache);

        System.out.println("Please Enter the Item Number to Delete:\t");
        deleteItemFile(sc.nextInt());
    }

    public static  boolean deleteItemFile(int deleteLinenumber)throws IOException{
        BufferedReader reader = null;
        boolean validationFailed = true;
        int count = 1;
        File file = new File(ITEM_DIRECTORY_NAME+"\\"+usernameCache);
        File tempFile = new File(ITEM_DIRECTORY_NAME+"\\"+usernameCache+".tmp");
        BufferedWriter writer  = null;
        if(file.exists()) {

            writer = new BufferedWriter(new FileWriter(tempFile));
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line = reader.readLine()) != null){
                if(deleteLinenumber==count) {
                    count++;
                    continue;
                }
                writer.write(line+"\n");
                count++;
            }
            reader.close();
            writer.close();
       boolean b =     file.delete();
        }

        tempFile.renameTo(file);
        return validationFailed;
    }


    public static  Float viewCheckoutOrBill(String flag,String fileName)throws IOException{
        BufferedReader reader;
        int count = 1;
        Float total =0f;
        File file = new File(fileName);

        if(file.exists()) {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while((line = reader.readLine()) != null){

                if(line.split(" ").length>1 ||line.contains("*")) {

                    if(flag !=null) {
                    total = total + Float.parseFloat(line.split(" ")[1]);
                        System.out.println(count + ".\t" + line);
                        System.out.print("\n");
                    }else{
                        System.out.println(line);
                    }

                }
                count++;
                }
                reader.close();

            if(flag != null){
                System.out.println("**********************************************\n");
                System.out.println("\t\tTotal: "+total);
                System.out.println("**********************************************\n");
            }
            }else{
            total = null;
        }

        return total;
    }

    public static void WriteItemIntoFile(String itemName,float price,int quantity)throws  IOException{
        File dir = new File(ITEM_DIRECTORY_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(ITEM_DIRECTORY_NAME+"\\"+usernameCache);
        FileWriter fw = null;
        BufferedWriter bw = null; PrintWriter pw = null;

        try {
            if (file.exists()) {
                fw = new FileWriter(file, true);

                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                pw.println(itemName + " " + price + " " + quantity);
                pw.flush();
            }else{
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(itemName + " " + price + " " + quantity+"\n");
                bw.close();
            }
            System.out.println("Item Added Successfully!");
        }
        finally {
            if(pw != null) {
                pw.close();
            }
            if(bw !=null) {
                bw.close();
            }
            fw.close();
        }
    }


    public static  String encryptPassword(String passwordToHash){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    return generatedPassword;
    }

    public static  boolean validateSignInfo(String username,String passwordToHash)throws FileNotFoundException,IOException{
    BufferedReader reader;
    boolean validationFailed = true;
        File file = new File(DIRECTORY_NAME+"\\"+SIGNUP_FILE_NAME);
        if(file.exists()) {
            reader = new BufferedReader(new FileReader(DIRECTORY_NAME + "\\" + SIGNUP_FILE_NAME));
            String line = null;
            String userInfoArray[] = null;
            while((line =reader.readLine()) !=null){


                userInfoArray = line.split(",");
                if(userInfoArray.length>1) {
                    if (username.equalsIgnoreCase(userInfoArray[2]) && passwordToHash.equalsIgnoreCase(userInfoArray[3])) {
                        validationFailed = false;
                        usernameCache = userInfoArray[2];
                        break;
                    }
                }
            }
        }
       if(validationFailed){
           usernameCache = "";
       }
    return validationFailed;
    }

    public static void displaySubMenu(){
        StringBuffer subMenu = new StringBuffer();
        subMenu.append("\t\t[1]\tAdd Item\n");
        subMenu.append("\t\t[2]\tDelete Item\n");
        subMenu.append("\t\t[3]\tView My Bill\n");
        subMenu.append("\t\t[4]\tView All Bills\n");
        subMenu.append("\t\t[5]\tView Checkout\n");
        subMenu.append("\t\t[6]\tReturn\n");
        subMenu.append("**********************************************\n");
        System.out.print(subMenu.toString());

    }
}

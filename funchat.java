import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class funchat {
   private static int messagecount=0;
    public static void startchart(String sender){
        Scanner scanner=new Scanner(System.in);
        while (true){

            System.out.println("welcome to funchart");
            System.out.println("select option");
            System.out.println("1; select chat options");
            System.out.println("2;send message");
            System.out.println("3 quit");
System.out.println(" enter your choice");
int choice= scanner.nextInt();
String receiver="";
scanner.nextLine();
switch (choice){
    case 2:System.out.println("you have selected send message");
    do {
        System.out.println("enter receiver number(must start with +27 and have exactly 12 numbers)");
        receiver=scanner.nextLine();
    }while (receiver.startsWith("+27")&& receiver.length()<=12);
    System.out.println("enter your message it must be 250 characters or less");
    String message=scanner.nextLine();
            if (message.length()>250) {
                System.out.println("ensure that your message is 250 charecters of less");
                return;

            }
            String messageid =generatemessageid();
            int currentmessagecount=++messagecount;
            String messageHash=genertemassagehash(messageid,messagecount, message);
            System.out.println("messagehash"+messageHash);
            System.out.println("choose option");
            System.out.println("send message");
            System.out.println("store send message");
            System.out.println("delete message");
            int subchoice= scanner.nextInt();
            switch (subchoice){
                case 1:System.out.println("messagesent");
                break;
                case 2:System.out.println("message deleted");
                break;
                case 3:storemessage(messageid,receiver,message,messageHash);
                break;
                default:
                    System.out.println("invalid option");
            }
            break;




    case 3: System.out.println("quitting");


    return;
    default:System.out.println("invalid choice choose 1,2,3");
}

}
    }
    private static String generatemessageid(){
        Random random=new Random();
        String id ="";
        for(int i =0;i<10;i++){
            id+= random.nextInt(10);
        }
        return id;
    }
private static String genertemassagehash(String messageid,int count,String message){
        String[] words=message.trim().split("\\s+");
        String first=words.length>0? words[0]:"";
        String last=words.length>1? words[words.length-1]:first;
        return messageid.substring(0,2)+":"+count+":"+ first.toLowerCase()+last.toLowerCase();

    }
    private static void storemessage(String id,String receiver,String message,String hash){
        try(FileWriter fileWriter=new FileWriter("stored message.txt",true)) {
            fileWriter.write("messageid:"+id+"\n");
            fileWriter.write("receiver:"+receiver+"\n");
            fileWriter.write("message:"+message+"\n");
            fileWriter.write("hash:"+hash+"\n");
            fileWriter.write("-----\n");
            System.out.println("the message has been stored successfully");

        }catch(IOException e){
            e.printStackTrace();
        }

    }


}

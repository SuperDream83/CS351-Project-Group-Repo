public class Test {


    public static void main(String[] args) {
        String string = "SEND_MONEY_TO_USER" + "|" + "recipient" + "|" + "amount" + "|" + "username";
        String[] strings = string.split("\\|");

        int count = 0;
        for (String line : strings){
            count++;
            System.out.println(line);
        }

        System.out.println(count);
    }

}

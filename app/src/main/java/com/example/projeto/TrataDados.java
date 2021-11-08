package com.example.projeto;

public class TrataDados {

    //Converte a string recebida pelo Bluetooth em um vetor de double
    public static double[] parseData(String string) {
        double dados[] = new double[5];
        String[] stringDados = string.split(";");
        for (int i=0; i<stringDados.length; i++) {
            dados[i] = Double.parseDouble(stringDados[i]);
        }
        return dados;
    }

    public static double convertAnalog_Porcentagem(double valorAnalog) {
        return Math.round(valorAnalog*100/1023);
    }

    public static int getInt_toString(CharSequence str, int numChar) {
        String newStr = str.toString().substring(0, str.length()-numChar);
        return Integer.parseInt(newStr);
    }

}

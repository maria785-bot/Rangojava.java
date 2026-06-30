package main.java.util;

import exception.DocumentoInvalidoException;
import exception.SenhaInvalidaException;

public class ValidadorDocumentos {

    public static void validarCPF(String cpf) throws DocumentoInvalidoException {
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11 || !calcularDigitosVerificadores(cpf, 11)) {
            throw new DocumentoInvalidoException("CPF");
        }
    }

    public static void validarCNPJ(String cnpj) throws DocumentoInvalidoException {
        cnpj = cnpj.replaceAll("[^0-9]", "");
        if (cnpj.length() != 14 || !calcularDigitosVerificadores(cnpj, 14)) {
            throw new DocumentoInvalidoException("CNPJ");
        }
    }

    private static boolean calcularDigitosVerificadores(String documento, int tamanho) {
        int[] pesos1 = (tamanho == 11) ?
                new int[]{10,9,8,7,6,5,4,3,2} :
                new int[]{5,4,3,2,1,0,9,8,7,6,5,4,3,2};
        int[] pesos2 = (tamanho == 11) ?
                new int[]{11,10,9,8,7,6,5,4,3,2} :
                new int[]{6,5,4,3,2,1,0,9,8,7,6,5,4,3,2};

        int soma = 0;
        for(int i=0; i < pesos1.length; i++) soma += Integer.parseInt(documento.charAt(i)+"") * pesos1[i];
        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        soma = 0;
        for(int i=0; i < pesos2.length; i++) soma += Integer.parseInt(documento.charAt(i)+"") * pesos2[i];
        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        return documento.endsWith("" + digito1 + digito2);
    }

    public static void validarSenha(String senha) throws SenhaInvalidaException {
        if (senha.length() < 8 || !senha.matches(".*\\d.*")) {
            throw new SenhaInvalidaException();
        }
    }
}
package util;

// CORRIGIDO: Importações corretas
import exception.DocumentoInvalidoException;
import exception.SenhaInvalidaException;

public class ValidadorDocumentos {

    public static void validarCPF(String cpf) throws DocumentoInvalidoException {
        if (cpf == null) {
            throw new DocumentoInvalidoException("CPF não pode ser nulo.");
        }

        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            throw new DocumentoInvalidoException("CPF deve ter 11 dígitos.");
        }

        // Verificar se todos os dígitos são iguais (CPF inválido)
        if (cpf.matches("(\\d)\\1{10}")) {
            throw new DocumentoInvalidoException("CPF inválido: todos os dígitos são iguais.");
        }

        if (!calcularDigitosVerificadoresCPF(cpf)) {
            throw new DocumentoInvalidoException("CPF inválido: dígitos verificadores incorretos.");
        }
    }

    public static void validarCNPJ(String cnpj) throws DocumentoInvalidoException {
        if (cnpj == null) {
            throw new DocumentoInvalidoException("CNPJ não pode ser nulo.");
        }

        cnpj = cnpj.replaceAll("[^0-9]", "");
        if (cnpj.length() != 14) {
            throw new DocumentoInvalidoException("CNPJ deve ter 14 dígitos.");
        }

        // Verificar se todos os dígitos são iguais (CNPJ inválido)
        if (cnpj.matches("(\\d)\\1{13}")) {
            throw new DocumentoInvalidoException("CNPJ inválido: todos os dígitos são iguais.");
        }

        if (!calcularDigitosVerificadoresCNPJ(cnpj)) {
            throw new DocumentoInvalidoException("CNPJ inválido: dígitos verificadores incorretos.");
        }
    }

    private static boolean calcularDigitosVerificadoresCPF(String cpf) {
        // Primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Integer.parseInt(String.valueOf(cpf.charAt(i))) * (10 - i);
        }
        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        // Segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Integer.parseInt(String.valueOf(cpf.charAt(i))) * (11 - i);
        }
        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        // Verificar se os dígitos calculados são iguais aos fornecidos
        return cpf.charAt(9) == Character.forDigit(digito1, 10) &&
                cpf.charAt(10) == Character.forDigit(digito2, 10);
    }

    private static boolean calcularDigitosVerificadoresCNPJ(String cnpj) {
        // Primeiro dígito verificador
        int[] pesos1 = {5, 4, 3, 2, 1, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Integer.parseInt(String.valueOf(cnpj.charAt(i))) * pesos1[i];
        }
        int resto = soma % 11;
        int digito1 = (resto < 2) ? 0 : 11 - resto;

        // Segundo dígito verificador
        int[] pesos2 = {6, 5, 4, 3, 2, 1, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Integer.parseInt(String.valueOf(cnpj.charAt(i))) * pesos2[i];
        }
        resto = soma % 11;
        int digito2 = (resto < 2) ? 0 : 11 - resto;

        // Verificar se os dígitos calculados são iguais aos fornecidos
        return cnpj.charAt(12) == Character.forDigit(digito1, 10) &&
                cnpj.charAt(13) == Character.forDigit(digito2, 10);
    }

    public static void validarSenha(String senha) throws SenhaInvalidaException {
        if (senha == null || senha.isEmpty()) {
            throw new SenhaInvalidaException("Senha não pode ser vazia.");
        }

        if (senha.length() < 8) {
            throw new SenhaInvalidaException("Senha deve ter no mínimo 8 caracteres.");
        }

        if (!senha.matches(".*\\d.*")) {
            throw new SenhaInvalidaException("Senha deve conter pelo menos um número.");
        }

        if (!senha.matches(".*[A-Z].*")) {
            throw new SenhaInvalidaException("Senha deve conter pelo menos uma letra maiúscula.");
        }

        if (!senha.matches(".*[a-z].*")) {
            throw new SenhaInvalidaException("Senha deve conter pelo menos uma letra minúscula.");
        }

        if (!senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new SenhaInvalidaException("Senha deve conter pelo menos um caractere especial.");
        }
    }

    // Método utilitário para formatar CPF
    public static String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }

    // Método utilitário para formatar CNPJ
    public static String formatarCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) {
            return cnpj;
        }
        return cnpj.substring(0, 2) + "." +
                cnpj.substring(2, 5) + "." +
                cnpj.substring(5, 8) + "/" +
                cnpj.substring(8, 12) + "-" +
                cnpj.substring(12, 14);
    }

    // Método para limpar formatação (remover pontos, traços, barras)
    public static String limparFormatacao(String documento) {
        if (documento == null) {
            return null;
        }
        return documento.replaceAll("[^0-9]", "");
    }

    // Método para validar email
    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    // Método para validar telefone
    public static boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            return false;
        }
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
        return telefoneLimpo.length() >= 10 && telefoneLimpo.length() <= 11;
    }
}
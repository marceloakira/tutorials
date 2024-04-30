
/**
 * Modela o cálculo de IMC
 * 
 * @author marceloakira 
 * @version 0.01
 */
public class IMC
{
    double peso;
    double altura;
    
    public IMC() {
        this.peso = 0;
        this.altura = 0;
    }
    
    /**
     * Construtor para objetos da classe CalculadoraIMC
     */
    public IMC(double altura, double peso)
    {
        this.altura = altura;  
        this.peso = peso;
    }

    /**
     * Calcula o IMC
     * 
     * @return     o valor do IMC, dado por: peso / ( altura x altura)
     */
    public double calcular()
    {
        if (this.altura <= 0 || this.peso <= 0)
            return 0;
        return this.peso / (this.altura * this.altura);
    }
    
    /**
     * Interpreta o IMC
     * 
     * @return     interpreta o IMC
     * @see <a href="https://pt.wikipedia.org/wiki/%C3%8Dndice_de_massa_corporal">IMC Wikipedia</a>
     */
    public String interpretar()
    {
        String resultado = "";
        double imc = this.calcular();
        if (imc == 0)
            resultado += "Dados incompletos ou inválidos";
        else if (imc < 17)
            resultado += "Muito abaixo do peso";
        else if (imc < 18.49)
            resultado += "Abaixo do peso";
        else if (imc < 24.99)
            resultado += "Peso normal";
        else if (imc < 29.99)
            resultado += "Acima do peso";
        else if (imc < 34.99)
            resultado += "Obesidade I";            
        else if (imc < 39.99)
            resultado += "Obesidade II (severa)";
        else
            resultado += "Obesidade III (mórbida)";
        return resultado;
    }
}

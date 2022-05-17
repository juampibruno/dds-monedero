package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo ; // Innecesario ya se inicializa en 0 por defecto
  private List<Movimiento> movimientos = new ArrayList<>();

  //public Cuenta() {saldo = 0;} Puede hacer un solo constructor

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) { // LONG METHOD
    this.validarMontoPositivo(cuanto);
    this.validarDepositiosDiariosMaximo();
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
    saldo = saldo + cuanto;
  }

  public void sacar(double cuanto) { // LONG METHOD
    validarMontoPositivo(cuanto);
    validarSacarMenosDelSaldo(cuanto);
    validarExtraccionesDiarias(cuanto);
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
    saldo = saldo - cuanto;
  }

  public void validarDepositiosDiariosMaximo(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }
  public void validarMontoPositivo(double cuanto){
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void validarSacarMenosDelSaldo(double cuanto){
    if (getSaldo() <= cuanto) { // Innecesario restarlos lo que tendria que hacer directamente es getSaldo() < cuanto
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

  }

  public void validarExtraccionesDiarias(double cuanto){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy; // El limite lo tendria que tener en una variable asi aporta mayor flexibilidad
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public void agregarMovimiento(Movimiento unMovimiento) { //LONG PARAMETRER LIST
    movimientos.add(unMovimiento);
  }


  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  } // Mala practica este setter pq me podria romper todo el sistema
  // Es innecesario pq ya tengo constructor y para agregar o sacar saldo tendria que usar sacar o poner
}

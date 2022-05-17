package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta(0);
  }

  @Test
  @DisplayName("Pongo 1500 en una cuenta nueva y su saldo va a ser de 1500")
  void Poner() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(),1500);
  }

  @Test
  @DisplayName("No le puedo poner saldo negativo a una cuenta")
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  @DisplayName("Le cargo tres depositos a una cuenta nueva y su saldo se carga correctamente")
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900); //1500 + 456 + 1900 = 3856
    assertEquals(cuenta.getSaldo(),3856);
  }

  @Test
  @DisplayName("Si quiero hacer mas de 3 depositos en un dia, tira una excepcion y no me deja")
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  @DisplayName("Si quiero extrar mas que el saldo tira una excepcion y no me deja")
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  @DisplayName("Si quiero extraer mas de 1000 que es el limite diario tira una excepcion y no me deja")
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  @DisplayName("Si quiero extraer un monto negativo, no me deja y tira una excepcion")
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

}
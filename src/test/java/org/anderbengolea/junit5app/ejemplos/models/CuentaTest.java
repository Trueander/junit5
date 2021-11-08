package org.anderbengolea.junit5app.ejemplos.models;

import org.anderbengolea.junit5app.ejemplos.exceptions.DineroInsufucienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName() + " com las etiquetas " + testInfo.getTags());
        this.cuenta = new Cuenta("Anderson", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el método");
    }

    @AfterEach
    void endTests() {
        System.out.println("Finalizando el método");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("el nombre")
        void testNombreCuenta() {
            System.out.println(testInfo.getTags());

            if(testInfo.getTags().contains("cuenta")){
                System.out.println("HACER ALGO CON LA ETIQUETA");
            }

//        cuenta.setPersona("Anderson");
            String esperado = "Anderson";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "la cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "el nombre de la cuenta no es la que se esperaba");
            assertTrue(real.equals("Anderson"), () -> "nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        void testSaldoCuenta() {
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


        @Test
        void testReferenciaDeCuenta() {
            cuenta = new Cuenta("Pepe Grillo", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("Pepe Grillo", new BigDecimal("8900.9997"));

//        assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);

        }

    }

    @Nested
    class CuentaOperacionesTest {
        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }
    }




    @Test
    void testDineroInsufucienteExceptionCuenta() {
        Exception  exception = assertThrows(DineroInsufucienteException.class, () -> {
           cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Tag("cuenta")
    @Tag("banco")
    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Jose", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Ander", new BigDecimal("1500.2344"));

        Banco banco = new Banco();
        banco.setNombre("BBVA");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertEquals("1000.2344", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

    }

    @Test
    @Disabled
    void testRelacionBancoCuentas() {
        fail();
        Cuenta cuenta1 = new Cuenta("Jose", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Ander", new BigDecimal("1500.2344"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("BBVA");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(() -> {
            assertEquals("1000.2344", cuenta2.getSaldo().toPlainString(), () -> "El valor de saldo de la cuenta2 no es el esperado.");
        }, () -> {
            assertEquals("3000", cuenta1.getSaldo().toPlainString(), () -> "El valor de saldo de la cuenta1 no es el esperado.");
        }, () -> {
            assertEquals(2, banco.getCuentas().size(), () -> "el banco no tiene las cuentas esperadas");
        }, () -> {
            assertEquals("BBVA", cuenta1.getBanco().getNombre());
        }, () -> {
            assertEquals("Ander", banco.getCuentas().stream()
                    .filter(c -> c.getPersona().equals("Ander"))
                    .findFirst()
                    .get().getPersona());
        }, () -> {
            assertTrue(banco.getCuentas().stream()
                    .anyMatch(c -> c.getPersona().equals("Jose")));
        });
    }

    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {

        }
        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloMacLinux() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {

        }

    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJDK8() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void soloJDK11() {

        }

        @Test
        @DisabledOnJre(JRE.JAVA_11)
        void TestNoJDK11() {

        }
    }

    @Nested
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "11.0.12")
        void testJavaVersion() {

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {

        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "ANDER")
        void testSoloAnderson () {

        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev () {

        }
    }




    @Test
    @DisplayName("probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado.")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);

        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test saldo cuenta dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });


        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando Débito cuenta repetir!")
    @RepeatedTest(value = 5, name = "{displayName} - Repetición {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {

        if(info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repetición:" + info.getCurrentRepetition());
        }

        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest {

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} {argumentsWithNames}")
    @ValueSource(strings = {"100","200","300","500", "700", "1000.12345"})
    void testDebitoCuenta(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} {argumentsWithNames}")
    @CsvSource({"1,100","2,200","3,300","4,500", "5,700", "6,1000.12345"})
    void testDebitoCuentaCsvSource(String index, String monto) {
        System.out.println(index + " -> " + monto);
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} {argumentsWithNames}")
    @CsvSource({"200,100,John,Andres","250,200,Pepe,Pepe","300,300,maria,Maria","510,500,Carlita,Carlita", "750,700,Jose,Jose", "1000.12345,1000.12345,Rosa,Rosa"})
    void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
        System.out.println(saldo + " -> " + monto);
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.debito(new BigDecimal(monto));
        cuenta.setPersona(actual);

        assertNotNull(cuenta.getPersona());
        assertNotNull(cuenta.getSaldo());
        assertEquals(esperado, actual);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    void testDebitoCuentaCsvFileSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "número {index} ejecutando con valor {0} {argumentsWithNames}")
    @CsvFileSource(resources = "/data2.csv")
    void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.debito(new BigDecimal(monto));
        cuenta.setPersona(actual);

        assertNotNull(cuenta.getPersona());
        assertNotNull(cuenta.getSaldo());
        assertEquals(esperado, actual);

        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    }


    @Tag("param")
    @ParameterizedTest(name = "número {index} ejecutando con valor {0} {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100","200","300","500", "700", "1000.12345");
    }


    @Tag("timeout")
    @Nested
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        void testTimeOutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.SECONDS.sleep(4);
            });
        }

    }

}
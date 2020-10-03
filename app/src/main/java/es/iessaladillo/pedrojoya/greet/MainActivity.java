package es.iessaladillo.pedrojoya.greet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import es.iessaladillo.pedrojoya.greet.databinding.MainActivityBinding;

/**
 * Clase principal de la aplicación.
 *
 * Muestra una interfaz de usuario en la que se puede introducir nombre y apellidos, para
 * obtener un saludo; el cual puede ser educado o informal. Además, permite cambiar a la
 * versión PRO de la aplicación.
 *
 * @author Manuel
 */
public class MainActivity extends AppCompatActivity {

    /**
        Clase autogenerada. Nos permitirá acceder a las vistas que compongan el layout actual.
     */
    MainActivityBinding binding;

    /**
        Contador del número de veces que se ha saludado.
        Se reinicia al pasar a premium.
     */
    private int counter = 0;

    /**
        Intento de constante. Delimita el número máximo de veces que se puede hacer uso
        del botón saludo siendo un usuario no premium.

        Se ha optado por poner el número como recurso de entero, dado que es un valor
        que no debería cambiar a lo largo de la aplicación.
     */
    private int maxIteration; // No he podido declararlo como final.

    /**
        Booleano que identifica si el saludo debe ser informal o formal.
     */
    private boolean isPolitely;

    /**
        Booleano que identifica si el usuario tiene privilegios o no.
     */
    private boolean isPremium;

    /**
        Booleano que determina el prefijo que se le pondrá al usuario si el saludo es
        educado.
     */
    private String greetPrefix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
            Cuando se llame al método, obtendremos el árbol de vistas para la interfaz
            y todas las vistas posibles para la misma, que se almacenará en binding.

            Además, se llamarán a diferentes funciones de configuración de la aplicación.
         */
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupFields(); // configura los campos.
        setupViews(); // configura las vistas
        actualizarNumeroSaludos(); // inicializa el mensaje de la cantidad de saludos. Por defecto 0.
    }

    /**
     * Inicializa los atributos de la clase que sean necesarios inicializar para el correcto
     * funcionamiento de la misma.
     */
    private void setupFields() {
        maxIteration = getResources().getInteger(R.integer.main_integer_maxGreet);
        greetPrefix = getString(R.string.rb_main_mr);
    }

    /**
     * Inicializa la configuración inicial de las vistas.
     *
     * Se ha definido el comportamiento de las vistas cuando el usuario interactúe con ellas.
     */
    private void setupViews() {
        // Acción cuando el usuario presione el botón.
        binding.btnMainGreet.setOnClickListener(l -> modificarNumeroDePulsaciones());

        // Acción cuando el usuario marque/desmarque la opción educado.
        binding.chkMainEducado.setOnCheckedChangeListener(this::actualizarSaludo);

        // Acción cuando el usuario marque/desmarque la opción premium.
        binding.swtMainPremium.setOnCheckedChangeListener(this::cambiarPremium);

        // Acción cuando el usuario cambie de opción en el RadioGroup.
        binding.rgMainSelectPrefix.setOnCheckedChangeListener(this::selectGreetPrefix);
    }

    /**
     * Este método determina si es posible para la aplicación seguir saludando. Dependiendo
     * de diversos factores como si el usuario es premium, si los campos necesarios
     * están rellenos, etc.
     */
    private void modificarNumeroDePulsaciones() {

        if(!binding.etxtMainName.getText().toString().equalsIgnoreCase("") &&
                !binding.etxtMainLastName.getText().toString().equalsIgnoreCase("")) {
            // Comprueba que los EditTexts estén rellenos. Si no lo están,
            // no se hace nada.

            if(isPremium) {
                // Para poder saludar el usuario debe ser premium o no haber alcanzado
                // el número máximo de saludos.
                saludar();

            } else if(counter < maxIteration) {
                counter++;
                actualizarNumeroSaludos();
                saludar();
            }else  {
                // Si no se puede saludar, se debe comprobar una subcripción.
                showBuyPremium();
            }
        }
    }

    /**
     * Si la aplicación pude saludar, este método se encarga de escribir el saludo.
     * Dicho saludo cambiará según si el usuario ha marcado la opción de saludo
     * formal o no.
     */
    private void saludar() {
        // Se obtiene el valor de los EditTexts
        String name = binding.etxtMainName.getText().toString();
        String lastName = binding.etxtMainLastName.getText().toString();

        if(isPolitely) {
            // Se comprueba si el saludo es informal o no.
            binding.txtMainGreet.setText(getString(R.string.txt_main_saludoFormal,
                    greetPrefix, name, lastName));
        } else {
            binding.txtMainGreet.setText(getString(R.string.txt_main_saludoInformal,
                    name, lastName));
        }

    }

    /**
     * Cuando el usuario marca o desmarca el checkBox de saludo, se llama a esta
     * función. La cual cambia el valor del campo isPolitely al mismo
     * valor que el checkBox
     * @param buttonView
     * @param isChecked
     */
    private void actualizarSaludo(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            isPolitely = true;
        } else {
            isPolitely = false;
        }
    }

    /**
     * Actualiza el número del saludo actual cuando el usuario saluda sin
     * ser premium
     */
    private void actualizarNumeroSaludos() {
        binding.txtMainGreetCounter.setText(getString(R.string.txt_main_d_of_10, counter, maxIteration));
        actualiarBarraProgresiva();
    }

    /**
     * Actualiza el progreso de la barra Progresiva, según el valor de
     * counter.
     */
    private void actualiarBarraProgresiva() {
        binding.prBMainHowGreet.setProgress(counter);
    }

    /**
     * Muestra un mensaje para comprar una subcripción premium cuando se alcanza
     * el número máximo de pruebas gratuitas.
     */
    private void showBuyPremium() {
        binding.txtMainGreet.setText(R.string.txt_main_buy_premium);
    }

    /**
     * Se activa al hacer uso del switch premium. Modifica el valor del campo
     * isPremium según el valor del switch.
     * @param buttonView
     * @param isChecked
     */
    private void cambiarPremium(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            isPremium = true;
        } else {
            isPremium = false;
        }

        mostrarEsconderBarraProgresiva(isChecked);
        // Se llama al método para mostrar o esconder la barra, según convenga.
    }

    /**
     * Cuando es llamado, desde el método cambiar premium, esconderá o mostrará la barra
     * según el valor del booleano pasado por dicho método.
     * @param isChecked
     */
    private void mostrarEsconderBarraProgresiva(boolean isChecked) {
        if(isChecked) {
            binding.llMainPackPrB.setVisibility(View.GONE);
        } else {
            binding.llMainPackPrB.setVisibility(View.VISIBLE);
            binding.txtMainGreet.setText(""); // Reinicializa el mensaje.
        }
        counter = 0; // Reinicializa el contador.
        actualizarNumeroSaludos(); // Actualiza el número de saludo, reinicia a 0.
    }

    /**
     * Determina que RadioButton está pulsado y con ello, que prefijo se debe usar
     * cuando el saludo sea formal.
     * @param radioGroup
     * @param i
     */
    private void selectGreetPrefix(RadioGroup radioGroup, int i) {
        if(radioGroup.getCheckedRadioButtonId() == binding.rbMainMr.getId()) {
            greetPrefix = getString(R.string.rb_main_mr);
            binding.imageMainAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_mr));
        } else if(radioGroup.getCheckedRadioButtonId() == binding.rbMainMrs.getId()) {
            greetPrefix = getString(R.string.rb_main_mrs);
            binding.imageMainAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_mrs));
        } else {
            greetPrefix = getString(R.string.rb_main_ms);
            binding.imageMainAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ms));

        }
    }
}// Fin de la clase
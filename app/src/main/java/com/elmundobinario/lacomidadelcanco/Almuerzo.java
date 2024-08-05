package com.elmundobinario.lacomidadelcanco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Almuerzo extends AppCompatActivity {

    ImageView imagenAlmuerzoButton;

    TextView preguntaIngrediente;

    // base de datos con SharedPreferences:
    SharedPreferences cerealesSharedPreferences;
    SharedPreferences legumbresSharedPreferences;
    SharedPreferences hortalizasTuberculosSharedPreferences;
    SharedPreferences condimentoHidratosSharedPreferences;
    SharedPreferences.Editor sharedPrefEditorCereales;
    SharedPreferences.Editor sharedPrefEditorLegumbres;
    SharedPreferences.Editor sharedPrefEditorHortalizasTuberculos;
    SharedPreferences.Editor sharedPrefEditorCondimentoHidratos;

    //Aquí cargarán provisionalmente los arrays:
    String[] cereales = {"arroz integral#140 gramos.$22 minutos.", "pasta integral#110 gramos.$12 minutos.",
            "trigo sarraceno#120 gramos.$18 minutos.", "quinoa#120 gramos.$9 minutos.",
            "amaranto#100 gramos.$10 minutos.", "arroz blanco#150 gramos.$20 minutos.",
            "maiz#150 gramos.$En crudo.", "mijo#110 gramos.$9 minutos."};
    String[] legumbres = {"soja verde en grano#100 gramos.$8 minutos.",
            "azuki#50 gramos.$20 minutos.",
            "lentejas#90 gramos.$11 minutos.",
            "alubias oscuras#50 gramos.$13 minutos.",
            "habas#120 gramos$En crudo.", "altramuces#100 gramos.$5 minutos.",
            "garbanzos#70 gramos.$10 minutos.", "guisantes#120 gramos.$En crudo.",
            "cacahuetes#50 gramos.$En crudo."};
    String[] hortalizas_tuberculos = {"coliflor#200 gramos.$6 minutos.",
            "alcachofas#200 gramos.$10 minutos.", "calabacín#250 gramos.$12 minutos.",
            "remolacha#200 gramos.$20 minutos.", "nabo#200 gramos.$20 minutos.",
            "puerro#200 gramos.$15 minutos.", "pepino#200 gramos.$En crudo.",
            "pimiento de cualquier color#200 gramos.$En crudo.",
            "berenjena#250 gramos.$15 minutos.", "batata#250 gramos.$18 minutos.",
            "patata#250 gramos.$22 minutos.", "rábanos#200 gramos.$En crudo.",
            "chirivía#200 gramos.$20 minutos.", "calabaza#200 gramos.$17 minutos."};
    String[] condimento_hidratos = {"laurel#3 hojas.$A la olla.",
            "hierbabuena#Media cucharilla.$Con el cocimiento",
            "comino (de bote)#1/2 cucharilla.$En crudo.",
            "pimentón dulce (de bote)#1/2 cucharilla.$En crudo.",
            "cúrcuma (de bote)#1/2 cucharilla.$En crudo.",
            "jengibre#1/2cucharilla.$en la olla.",
            "mejorana#1/2 cucharilla.$En la olla."};

    int cerealPreguntado = 0;
    int legumbrePreguntada = 0;
    int hortaliza_tuberculoPreguntado = 0;
    int condimento_hidratosPreguntado = 0;

    String cerealElegido;
    String legumbreElegida;
    String hortalizaTuberculoElegido;
    String condimento_hidratosElegido;
    int ordenBloqueAlimento = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almuerzo);

        imagenAlmuerzoButton = findViewById(R.id.imagen_seleccion_almuerzo_buttonid);

        preguntaIngrediente = findViewById(R.id.ingrediente_almuerzo_textview);

        cerealesSharedPreferences = getSharedPreferences("cereales", Context.MODE_PRIVATE);
        legumbresSharedPreferences = getSharedPreferences("legumbres", Context.MODE_PRIVATE);
        hortalizasTuberculosSharedPreferences = getSharedPreferences("hortalizas_tuberculos", Context.MODE_PRIVATE);
        condimentoHidratosSharedPreferences = getSharedPreferences("condimento_hidratos", Context.MODE_PRIVATE);
        sharedPrefEditorCereales = cerealesSharedPreferences.edit();
        sharedPrefEditorLegumbres = legumbresSharedPreferences.edit();
        sharedPrefEditorHortalizasTuberculos = hortalizasTuberculosSharedPreferences.edit();
        sharedPrefEditorCondimentoHidratos = condimentoHidratosSharedPreferences.edit();

        // SE inicializan para evitar error al lanzar MenuFinal:
        cerealElegido = "(No hay cereales)# $ ";
        legumbreElegida = "(No hay legumbres)# $ ";
        hortalizaTuberculoElegido = "(No hay hortalizas)# $ ";
        condimento_hidratosElegido = "(No hay condimentos)# $ ";

        //Si no es la primera vez, existirá el fichero CEREALES y habrá que cargar el array desde SharedPreferences
        if (cerealesSharedPreferences.contains(String.valueOf(0))) {
            for (int i = 0; i < 8; i++) {
                cereales[i] = cerealesSharedPreferences.getString(String.valueOf(i), "");
            }

        } //fin de la carga del array cereales

        //Si no es la primera vez, existirá el fichero LEGUMBRES y habrá que cargar el array desde SharedPreferences
        if (legumbresSharedPreferences.contains(String.valueOf(0))) {
            for (int i = 0; i < 9; i++) {
                legumbres[i] = legumbresSharedPreferences.getString(String.valueOf(i), "");
            }
        } //fin de la carga del array legumbres

        //Si no es la primera vez, existirá el fichero HORTALIZAS_TUBERCULOS y habrá que cargar el array desde SharedPreferences:
        if (hortalizasTuberculosSharedPreferences.contains(String.valueOf(0))) {
            for (int i = 0; i < 14; i++) {
                hortalizas_tuberculos[i] = hortalizasTuberculosSharedPreferences.getString(String.valueOf(i), "");
            } //fin de la carga del array hortalizas_tuberculos
        }
        //Si no es la primera vez, existirá el fichero CONDIMENTOS_HIDRATOS y habrá que cargar el array desde SharedPreferences:
        if (condimentoHidratosSharedPreferences.contains(String.valueOf(0))) {
            for (int i = 0; i < 7; i++) {
                condimento_hidratos[i] = condimentoHidratosSharedPreferences.getString(String.valueOf(i), "");
            } //fin de la carga del array condimentos_hidratos.
        }
        // primera vez que muestra ingrediente preguntado. Las demás veces en onClicks...
        if (ordenBloqueAlimento == 1) {
            preguntaIngrediente.setText(alimento(cereales[cerealPreguntado]) + "?");
            mostrarImagenSeleccionAlimentoAlmuerzo(alimento(cereales[cerealPreguntado]));
        }

    }

    public void clickSi_Almuerzo(View view) {
        // 1 = cereales
        if (ordenBloqueAlimento == 1) {
            cerealElegido = cereales[cerealPreguntado];
            rotarArrayAlimento();
            ordenBloqueAlimento++;
            preguntaIngrediente.setText(alimento(legumbres[legumbrePreguntada]) + "?");
            mostrarImagenSeleccionAlimentoAlmuerzo(alimento(legumbres[legumbrePreguntada]));
            cerealPreguntado = 0;
        }
        // 2 = legumbres:
        else if (ordenBloqueAlimento == 2) {
            legumbreElegida = legumbres[legumbrePreguntada];
            rotarArrayAlimento();
            ordenBloqueAlimento++;
            preguntaIngrediente.setText(alimento(hortalizas_tuberculos[hortaliza_tuberculoPreguntado]) + "?");
            mostrarImagenSeleccionAlimentoAlmuerzo(alimento(hortalizas_tuberculos[hortaliza_tuberculoPreguntado]));
            legumbrePreguntada = 0;
        }
        // 3 = hortalizas_tuberculos:
        else if (ordenBloqueAlimento == 3) {
            hortalizaTuberculoElegido = hortalizas_tuberculos[hortaliza_tuberculoPreguntado];
            rotarArrayAlimento();
            ordenBloqueAlimento++;
            preguntaIngrediente.setText(alimento(condimento_hidratos[condimento_hidratosPreguntado]) + "?");
            mostrarImagenSeleccionAlimentoAlmuerzo(alimento(condimento_hidratos[condimento_hidratosPreguntado]));
        }
        // 4 = condimento_hidratos:
        else if (ordenBloqueAlimento == 4) {
            condimento_hidratosElegido = condimento_hidratos[condimento_hidratosPreguntado];
            rotarArrayAlimento();
            // Aqui ya se tienen todos los alimentos
            // AQUI VA EL DÓDIGO PARA GUARDAR DATOS DE ARRAYS EN ARCHIVOS
            guardarArrayCereales();
            guardarArrayLegumbres();
            guardarArrayHortalizaTuberculo();
            guardarArrayCondimentoHidratos();
            condimento_hidratosPreguntado = 0;
            lanzaActivityMenuFinal();
        }

    }

    public void clickNo_Almuerzo(View view) {
        if (ordenBloqueAlimento == 1) { // si es un cereal:
            cerealPreguntado++;        // pasa a preguntar el siguiente cereal
            if (cerealPreguntado >= 8) { // ya no hay más cereales: pasa a legumbres...
                ordenBloqueAlimento++;
                preguntaIngrediente.setText(alimento(legumbres[legumbrePreguntada]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(legumbres[legumbrePreguntada]));
                cerealPreguntado = 0;
            } else {
                preguntaIngrediente.setText(alimento(cereales[cerealPreguntado]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(cereales[cerealPreguntado]));
            }
        } else if (ordenBloqueAlimento == 2) { // si es una legumbre:
            legumbrePreguntada++;        // pasa a preguntar la siguiente legumbre:
            if (legumbrePreguntada >= 9) {
                ordenBloqueAlimento++; // ya no hay más cereales: pasa a legumbres...
                preguntaIngrediente.setText(alimento(hortalizas_tuberculos[hortaliza_tuberculoPreguntado]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(hortalizas_tuberculos[hortaliza_tuberculoPreguntado]));
                legumbrePreguntada = 0;
            } else {
                preguntaIngrediente.setText(alimento(legumbres[legumbrePreguntada]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(legumbres[legumbrePreguntada]));
            }

        } else if (ordenBloqueAlimento == 3) { // si es una hortaliza:
            hortaliza_tuberculoPreguntado++; // pasa a preguntar la siguiente hortaliza:
            if (hortaliza_tuberculoPreguntado >= 14) {
                ordenBloqueAlimento++; // ya no hay más cereales: pasa a legumbres...
                preguntaIngrediente.setText(alimento(condimento_hidratos[condimento_hidratosPreguntado]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(condimento_hidratos[condimento_hidratosPreguntado]));
                hortaliza_tuberculoPreguntado = 0;
            } else {
                preguntaIngrediente.setText(alimento(hortalizas_tuberculos[hortaliza_tuberculoPreguntado]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(hortalizas_tuberculos[hortaliza_tuberculoPreguntado]));
            }

        } else if (ordenBloqueAlimento == 4) { // si es un condimento:
            condimento_hidratosPreguntado++; // pasa a preguntar el siguiente condimento:
            if (condimento_hidratosPreguntado >= 7) {
                // Aqui ya se tienen todos los alimentos
                // AQUI VA EL DÓDIGO PARA GUARDAR DATOS DE ARRAYS EN ARCHIVOS
                guardarArrayCereales();
                guardarArrayLegumbres();
                guardarArrayHortalizaTuberculo();
                guardarArrayCondimentoHidratos();
                condimento_hidratosPreguntado = 0;
                lanzaActivityMenuFinal();
            } else {
                preguntaIngrediente.setText(alimento(condimento_hidratos[condimento_hidratosPreguntado]) + "?");
                mostrarImagenSeleccionAlimentoAlmuerzo(alimento(condimento_hidratos[condimento_hidratosPreguntado]));
            }

        }
    }

    private void rotarArrayAlimento() {
        // ROTAR CEREALES:
        if (ordenBloqueAlimento == 1) {
            String reordenandoCereales[] = new String[8];
            reordenandoCereales[7] = cereales[cerealPreguntado];
            int siguienteCerealPreguntadoEnIf = 0;
            for (int i = 0; i < 7; i++) {
                if (i == cerealPreguntado) siguienteCerealPreguntadoEnIf++; // salta el indice vacio
                // del indice que se mueve al final (ultimo indice del array)
                reordenandoCereales[i] = cereales[siguienteCerealPreguntadoEnIf];
                siguienteCerealPreguntadoEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 8; i++) {
                cereales[i] = reordenandoCereales[i];
            }
        }
        //ROTAR LEGUMBRES:
        else if (ordenBloqueAlimento == 2) {
            String reordenandoLegumbres[] = new String[9];
            reordenandoLegumbres[8] = legumbres[legumbrePreguntada];
            int siguienteLegumbrePreguntadaEnIf = 0;
            for (int i = 0; i < 8; i++) {
                if (i == legumbrePreguntada) siguienteLegumbrePreguntadaEnIf++;
                reordenandoLegumbres[i] = legumbres[siguienteLegumbrePreguntadaEnIf];
                siguienteLegumbrePreguntadaEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 9; i++) {
                legumbres[i] = reordenandoLegumbres[i];
            }

        }
        //ROTAR HORTALIZAS_TUBERCULOS:
        else if (ordenBloqueAlimento == 3) {
            String reordenandoHortalizasTuberculos[] = new String[14];
            reordenandoHortalizasTuberculos[13] = hortalizas_tuberculos[hortaliza_tuberculoPreguntado];
            int siguienteHortalizaPreguntadaEnIf = 0;
            for (int i = 0; i < 13; i++) {
                if (i == hortaliza_tuberculoPreguntado) siguienteHortalizaPreguntadaEnIf++;
                reordenandoHortalizasTuberculos[i] = hortalizas_tuberculos[siguienteHortalizaPreguntadaEnIf];
                siguienteHortalizaPreguntadaEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 14; i++) {
                hortalizas_tuberculos[i] = reordenandoHortalizasTuberculos[i];
            }

        }
        //ROTAR CONDIMENTO_HIDRATOS:
        else if (ordenBloqueAlimento == 4) {
            String reordenandoCondimento_Hidratos[] = new String[7];
            reordenandoCondimento_Hidratos[6] = condimento_hidratos[condimento_hidratosPreguntado];
            int siguienteCondimentoPreguntadoEnIf = 0;
            for (int i = 0; i < 6; i++) {
                if (i == condimento_hidratosPreguntado) siguienteCondimentoPreguntadoEnIf++;
                reordenandoCondimento_Hidratos[i] = condimento_hidratos[siguienteCondimentoPreguntadoEnIf];
                siguienteCondimentoPreguntadoEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 7; i++) {
                condimento_hidratos[i] = reordenandoCondimento_Hidratos[i];
            }

        }

    }

    private void guardarArrayCereales() {
        for (int i = 0; i < 8; i++) {
            sharedPrefEditorCereales.putString(String.valueOf(i), cereales[i]);
            sharedPrefEditorCereales.commit();
        }
    }

    private void guardarArrayLegumbres() {
        for (int i = 0; i < 9; i++) {
            sharedPrefEditorLegumbres.putString(String.valueOf(i), legumbres[i]);
            sharedPrefEditorLegumbres.commit();
        }
    }

    private void guardarArrayHortalizaTuberculo() {
        for (int i = 0; i < 14; i++) {
            sharedPrefEditorHortalizasTuberculos.putString(String.valueOf(i), hortalizas_tuberculos[i]);
            sharedPrefEditorHortalizasTuberculos.commit();
        }
    }

    private void guardarArrayCondimentoHidratos() {
        for (int i = 0; i < 7; i++) {
            sharedPrefEditorCondimentoHidratos.putString(String.valueOf(i), condimento_hidratos[i]);
            sharedPrefEditorCondimentoHidratos.commit();
        }
    }

    public String alimento(String textoCompleto) {
        String alimento = textoCompleto.substring(0, textoCompleto.indexOf("#"));
        return alimento;
    }

    public void mostrarImagenSeleccionAlimentoAlmuerzo(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "arroz integral":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arroz_integral_alimento, null));
                break;
            case "pasta integral":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pasta_integral_alimento, null));
                break;
            case "trigo sarraceno":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.trigo_sarraceno_alimento, null));
                break;
            case "quinoa":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.quinoa_alimento, null));
                break;
            case "amaranto":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.amaranto_alimento, null));
                break;
            case "arroz blanco":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arroz_blanco_alimento, null));
                break;
            case "maiz":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maiz_alimento, null));
                break;
            case "mijo":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mijo_alimento, null));
                break;
            case "soja verde en grano":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.soja_alimento, null));
                break;
            case "azuki":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.azuki_alimento, null));
                break;
            case "lentejas":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.lentejas_alimento, null));
                break;
            case "habas":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.habas_alimento, null));
                break;
            case "altramuces":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.altramuces_alimento, null));
                break;
            case "garbanzos":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.garbanzos_alimentos, null));
                break;
            case "guisantes":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.guisantes_alimentos, null));
                break;
            case "alubias oscuras":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.alubias_alimento, null));
                break;
            case "cacahuetes":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cacahuetes_alimentos, null));
                break;
            case "coliflor":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.coliflor_alimentos, null));
                break;
            case "alcachofas":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.alcachofa_alimentos, null));
                break;
            case "calabacín":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.calabacin, null));
                break;
            case "remolacha":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.remolacha_alimentos, null));
                break;
            case "nabo":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nabo_alimentos, null));
                break;
            case "puerro":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.puerro_alimentos, null));
                break;
            case "pepino":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pepino_alimentos, null));
                break;
            case "pimiento de cualquier color":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pimientos_alimentos, null));
                break;
            case "berenjena":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.berenjena_alimentos, null));
                break;
            case "batata":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.batata_alimentos, null));
                break;
            case "patata":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.patata_alimentos, null));
                break;
            case "rábanos":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rabano_alimentos, null));
                break;
            case "chirivía":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.chirivia_alimentos, null));
                break;
            case "calabaza":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.calabaza_alimentos, null));
                break;
            case "laurel":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.laurel_alimentos, null));
                break;
            case "hierbabuena":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hiervabuena_alimentos, null));
                break;
            case "comino (de bote)":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.comino_alimentos, null));
                break;
            case "pimentón dulce (de bote)":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pimenton_dulce_alimentos, null));
                break;
            case "cúrcuma (de bote)":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.curcuma_alimentos, null));
                break;
            case "jengibre":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.jengibre_alimentos, null));
                break;
            case "mejorana":
                imagenAlmuerzoButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mejorana_alimentos, null));
                break;
        }

    }


    private void lanzaActivityMenuFinal() {
        Intent intencion = new Intent(this, MenuAlmuerzoFinal.class);
        intencion.putExtra("cerealElegidoExtra", cerealElegido);
        intencion.putExtra("legumbreElegidaExtra", legumbreElegida);
        intencion.putExtra("hortalizaTuberculoElegidoExtra", hortalizaTuberculoElegido);
        intencion.putExtra("condimentoHidratosElegidoExtra", condimento_hidratosElegido);
        startActivity(intencion);
    }

}

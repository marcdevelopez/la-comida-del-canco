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
import android.widget.Toast;

public class MenuAlmuerzoFinal extends AppCompatActivity {

    public String cerealElegido;
    public String legumbreElegida;
    public String hortalizaTuberculoElegido;
    public String condimento_hidratosElegido;

    public String verduraElegida;
    public String proteinaElegida;
    public String condimentoCenaElegido;

    SharedPreferences cerealesSharedPreferences;
    SharedPreferences legumbresSharedPreferences;
    SharedPreferences hortalizasTuberculosSharedPreferences;
    SharedPreferences condimentoHidratosSharedPreferences;
    SharedPreferences.Editor sharedPrefEditorCereales;
    SharedPreferences.Editor sharedPrefEditorLegumbres;
    SharedPreferences.Editor sharedPrefEditorHortalizasTuberculos;
    SharedPreferences.Editor sharedPrefEditorCondimentoHidratos;

    SharedPreferences verduraSharedPreferences;
    SharedPreferences proteinaSharedPreferences;
    SharedPreferences condimentoCenaSharedPreferences;

    TextView mamaPonmeEstoTextView;

    TextView cerealElegidoTextView;
    TextView legumbreElegidaTextView;
    TextView hortalizaElegidaTextView;
    TextView condimentoElegidoTextView;

    TextView peso_tiempoCerealTextView;
    TextView peso_tiempoLegumbreTextView;
    TextView peso_tiempoHortalizaTextView;
    TextView peso_tiempoCondimentoTextView;

    char flechas = 187;

    ImageView imagenCerealFinal;
    ImageView imagenLegumbreFinal;
    ImageView imagenHortalizaFinal;
    ImageView imagenCondimentoFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_almuerzo_final);

        cerealesSharedPreferences = getSharedPreferences("cereales", Context.MODE_PRIVATE);
        legumbresSharedPreferences = getSharedPreferences("legumbres", Context.MODE_PRIVATE);
        hortalizasTuberculosSharedPreferences = getSharedPreferences("hortalizas_tuberculos", Context.MODE_PRIVATE);
        condimentoHidratosSharedPreferences = getSharedPreferences("condimento_hidratos", Context.MODE_PRIVATE);

        verduraSharedPreferences = getSharedPreferences("verduras_cena", Context.MODE_PRIVATE);
        proteinaSharedPreferences = getSharedPreferences("tipo_proteina", Context.MODE_PRIVATE);
        condimentoCenaSharedPreferences = getSharedPreferences("condimentos_cena", Context.MODE_PRIVATE);

        sharedPrefEditorCereales = cerealesSharedPreferences.edit();
        sharedPrefEditorLegumbres = legumbresSharedPreferences.edit();
        sharedPrefEditorHortalizasTuberculos = hortalizasTuberculosSharedPreferences.edit();
        sharedPrefEditorCondimentoHidratos = condimentoHidratosSharedPreferences.edit();

        imagenCerealFinal = findViewById(R.id.imagen_cereal_button);
        imagenLegumbreFinal = findViewById(R.id.imagen_legumbre_button);
        imagenHortalizaFinal = findViewById(R.id.imagen_hortaliza_button);
        imagenCondimentoFinal = findViewById(R.id.imagen_condimento_almuerzo_button);

        mamaPonmeEstoTextView = (TextView) findViewById(R.id.mama_ponme_esto);

        cerealElegidoTextView = (TextView) findViewById(R.id.cerealTextView);
        legumbreElegidaTextView = (TextView) findViewById(R.id.legumbreTextView);
        hortalizaElegidaTextView = (TextView) findViewById(R.id.hortalizaTextView);
        condimentoElegidoTextView = (TextView) findViewById(R.id.condimentoAlmuerzoTextView);

        peso_tiempoCerealTextView = (TextView) findViewById(R.id.cantidadCerealTextView);
        peso_tiempoLegumbreTextView = (TextView) findViewById(R.id.cantidadLegumbreTextView);
        peso_tiempoHortalizaTextView = (TextView) findViewById(R.id.cantidadHortalizaTextView);
        peso_tiempoCondimentoTextView = (TextView) findViewById(R.id.cantidadCondimentoAlmuerzoTextView);

        try {
            // Cargamos las variables de la actividad Almuerzo de los Extras del Intent:
            cerealElegido = getIntent().getExtras().getString("cerealElegidoExtra");
            legumbreElegida = getIntent().getExtras().getString("legumbreElegidaExtra");
            hortalizaTuberculoElegido = getIntent().getExtras().getString("hortalizaTuberculoElegidoExtra");
            condimento_hidratosElegido = getIntent().getExtras().getString("condimentoHidratosElegidoExtra");

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        cerealElegidoTextView.setText(alimento(cerealElegido));
        if (cerealElegido.equals("(No hay cereales)# $ ")) {
            imagenCerealFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenCerealAlmuerzo(alimento(cerealElegido));
        }
        //guardamos ingrediente gastado en SharedPreferences:
        sharedPrefEditorCereales.putString("ingrediente_usado_cereal", cerealElegido);
        sharedPrefEditorCereales.commit();

        legumbreElegidaTextView.setText(alimento(legumbreElegida));
        if (legumbreElegida.equals("(No hay legumbres)# $ ")) {
            imagenLegumbreFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenLegumbreAlmuerzo(alimento(legumbreElegida));
        }
        sharedPrefEditorLegumbres.putString("ingrediente_usado_legumbre", legumbreElegida);
        sharedPrefEditorLegumbres.commit();

        hortalizaElegidaTextView.setText(alimento(hortalizaTuberculoElegido));
        if (hortalizaTuberculoElegido.equals("(No hay hortalizas)# $ ")) {
            imagenHortalizaFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenHortalizasAlmuerzo(alimento(hortalizaTuberculoElegido));
        }
        sharedPrefEditorHortalizasTuberculos.putString("ingrediente_usado_hortaliza", hortalizaTuberculoElegido);
        sharedPrefEditorHortalizasTuberculos.commit();

        condimentoElegidoTextView.setText(alimento(condimento_hidratosElegido));
        if (condimento_hidratosElegido.equals("(No hay condimentos)# $ ")) {
            imagenCondimentoFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenCondimentoAlmuerzo(alimento(condimento_hidratosElegido));
        }
        sharedPrefEditorCondimentoHidratos.putString("ingrediente_usado_condimento", condimento_hidratosElegido);
        sharedPrefEditorCondimentoHidratos.commit();

        peso_tiempoCerealTextView.setText(flechas + " " + peso(cerealElegido) + "\n" + flechas + " " + tiempo(cerealElegido));
        peso_tiempoLegumbreTextView.setText(flechas + " " + peso(legumbreElegida) + "\n" + flechas + " " + tiempo(legumbreElegida));
        peso_tiempoHortalizaTextView.setText(flechas + " " + peso(hortalizaTuberculoElegido) + "\n" + flechas + " " + tiempo(hortalizaTuberculoElegido));
        peso_tiempoCondimentoTextView.setText(flechas + " " + peso(condimento_hidratosElegido) + "\n" + flechas + " " + tiempo(condimento_hidratosElegido));


        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Mantiene pantalla encendida...

        /* para apagar la pantalla probar esto, aun no se si funciona...
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

         */

    }

    public String alimento(String textoCompleto) {
        String alimento = textoCompleto.substring(0, textoCompleto.indexOf("#"));
        return alimento;
    }

    public String peso(String textoCompleto) {
        String alimento = textoCompleto.substring(textoCompleto.indexOf("#") + 1, textoCompleto.indexOf("$"));
        return alimento;
    }

    public String tiempo(String textoCompleto) {
        String alimento = textoCompleto.substring(textoCompleto.indexOf("$") + 1, textoCompleto.length());
        return alimento;
    }

    public void mostrarImagenCerealAlmuerzo(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "arroz integral":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arroz_integral_alimento, null));
                break;
            case "pasta integral":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pasta_integral_alimento, null));
                break;
            case "trigo sarraceno":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.trigo_sarraceno_alimento, null));
                break;
            case "quinoa":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.quinoa_alimento, null));
                break;
            case "amaranto":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.amaranto_alimento, null));
                break;
            case "arroz blanco":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arroz_blanco_alimento, null));
                break;
            case "maiz":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maiz_alimento, null));
                break;
            case "mijo":
                imagenCerealFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mijo_alimento, null));
                break;

        }
    }

    public void mostrarImagenLegumbreAlmuerzo(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "soja verde en grano":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.soja_alimento, null));
                break;
            case "azuki":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.azuki_alimento, null));
                break;
            case "lentejas":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.lentejas_alimento, null));
                break;
            case "habas":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.habas_alimento, null));
                break;
            case "altramuces":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.altramuces_alimento, null));
                break;
            case "garbanzos":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.garbanzos_alimentos, null));
                break;
            case "guisantes":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.guisantes_alimentos, null));
                break;
            case "alubias oscuras":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.alubias_alimento, null));
                break;
            case "cacahuetes":
                imagenLegumbreFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cacahuetes_alimentos, null));
                break;
        }
    }

    public void mostrarImagenHortalizasAlmuerzo(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "coliflor":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.coliflor_alimentos, null));
                break;
            case "alcachofas":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.alcachofa_alimentos, null));
                break;
            case "calabacín":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.calabacin, null));
                break;
            case "remolacha":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.remolacha_alimentos, null));
                break;
            case "nabo":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.nabo_alimentos, null));
                break;
            case "puerro":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.puerro_alimentos, null));
                break;
            case "pepino":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pepino_alimentos, null));
                break;
            case "pimiento de cualquier color":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pimientos_alimentos, null));
                break;
            case "berenjena":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.berenjena_alimentos, null));
                break;
            case "batata":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.batata_alimentos, null));
                break;
            case "patata":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.patata_alimentos, null));
                break;
            case "rábanos":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.rabano_alimentos, null));
                break;
            case "chirivía":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.chirivia_alimentos, null));
                break;
            case "calabaza":
                imagenHortalizaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.calabaza_alimentos, null));
                break;
        }
    }

    public void mostrarImagenCondimentoAlmuerzo(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "laurel":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.laurel_alimentos, null));
                break;
            case "hierbabuena":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hiervabuena_alimentos, null));
                break;
            case "comino (de bote)":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.comino_alimentos, null));
                break;
            case "pimentón dulce (de bote)":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pimenton_dulce_alimentos, null));
                break;
            case "cúrcuma (de bote)":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.curcuma_alimentos, null));
                break;
            case "jengibre":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.jengibre_alimentos, null));
                break;
            case "mejorana":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mejorana_alimentos, null));
                break;
        }
    }

    public void crearNuevaComidaDesdeMFinalAlmuerzo(View view) {
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
    }

    public void verUltimaCenaDesdeMFAlmuerzo(View view) {
        if (verduraSharedPreferences.contains("ingrediente_usado_verdura")) {
            verduraElegida = verduraSharedPreferences.getString("ingrediente_usado_verdura", "");
            proteinaElegida = proteinaSharedPreferences.getString("ingrediente_usado_proteina", "");
            condimentoCenaElegido = condimentoCenaSharedPreferences.getString("ingrediente_usado_condimento", "");
            Intent intencion = new Intent(this, MenuCenaFinal.class);
            intencion.putExtra("verduraElegidaExtra", verduraElegida);
            intencion.putExtra("alimentoProteinas", proteinaElegida);
            intencion.putExtra("condimentoCena", condimentoCenaElegido);
            startActivity(intencion);
        } else {
            Toast.makeText(this, "aun no hay última cena...", Toast.LENGTH_LONG).show();
        }

    }
}

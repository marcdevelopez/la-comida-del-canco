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

public class MenuCenaFinal extends AppCompatActivity {

    public String verduraElegidaExtra;
    public String alimentoProteinas;
    public String condimentoCena;

    public String cerealElegido;
    public String legumbreElegida;
    public String hortalizaTuberculoElegido;
    public String condimento_hidratosElegido;

    SharedPreferences verduraSharedPreferences;
    SharedPreferences proteinaSharedPreferences;
    SharedPreferences condimentoCenaSharedPreferences;
    SharedPreferences.Editor sharedPrefEditorVerdura;
    SharedPreferences.Editor sharedPrefEditorProteina;
    SharedPreferences.Editor sharedPrefEditorCondimentoCena;

    SharedPreferences cerealSharedPreferences;
    SharedPreferences legumbreSharedPreferences;
    SharedPreferences hortalizasTuberculosSharedPreferences;
    SharedPreferences condimentoHidratosSharedPreferences;

    TextView mamaPonmeEstoTextView;

    TextView verduraElegidaTextView;
    TextView proteinaElegidaTextView;
    TextView condimentoCenaElegidoTextView;

    TextView peso_tiempoVerduraTextView;
    TextView peso_tiempoProteinaTextView;
    TextView peso_tiempoCondimentoCenaTextView;

    char flechas = 187;

    ImageView imagenVerduraFinal;
    ImageView imagenProteinaFinal;
    ImageView imagenCondimentoFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_cena_final);

        verduraSharedPreferences = getSharedPreferences("verduras_cena", Context.MODE_PRIVATE);
        proteinaSharedPreferences = getSharedPreferences("tipo_proteina", Context.MODE_PRIVATE);
        condimentoCenaSharedPreferences = getSharedPreferences("condimentos_cena", Context.MODE_PRIVATE);

        cerealSharedPreferences = getSharedPreferences("cereales", Context.MODE_PRIVATE);
        legumbreSharedPreferences = getSharedPreferences("legumbres", Context.MODE_PRIVATE);
        hortalizasTuberculosSharedPreferences = getSharedPreferences("hortalizas_tuberculos", Context.MODE_PRIVATE);
        condimentoHidratosSharedPreferences = getSharedPreferences("condimento_hidratos", Context.MODE_PRIVATE);

        sharedPrefEditorVerdura = verduraSharedPreferences.edit();
        sharedPrefEditorProteina = proteinaSharedPreferences.edit();
        sharedPrefEditorCondimentoCena = condimentoCenaSharedPreferences.edit();

        imagenVerduraFinal = findViewById(R.id.imagen_verdura_final_buttonid);
        imagenProteinaFinal =  findViewById(R.id.imagen_proteina_final_buttonid);
        imagenCondimentoFinal = findViewById(R.id.imagen_condimento_cena_final_buttonid);

        mamaPonmeEstoTextView = (TextView) findViewById(R.id.mama_ponme_esto);

        verduraElegidaTextView = (TextView) findViewById(R.id.verduraTextView);
        proteinaElegidaTextView = (TextView) findViewById(R.id.proteinaTextView);
        condimentoCenaElegidoTextView = (TextView) findViewById(R.id.condimentoCenaTextView);

        peso_tiempoVerduraTextView = (TextView) findViewById(R.id.cantidadVerduraTextView);
        peso_tiempoProteinaTextView = (TextView) findViewById(R.id.cantidadProteinaTextView);
        peso_tiempoCondimentoCenaTextView = (TextView) findViewById(R.id.cantidadCondimentoCenaTextView);

        try {
            // Cargamos las variables de la actividad Cena de los Extras del Intent:
            verduraElegidaExtra = getIntent().getExtras().getString("verduraElegidaExtra");
            alimentoProteinas = getIntent().getExtras().getString("alimentoProteinas");
            condimentoCena = getIntent().getExtras().getString("condimentoCena");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        verduraElegidaTextView.setText(alimento(verduraElegidaExtra));
        if (verduraElegidaExtra.equals("(No hay verduras)# $ ")) {
            imagenVerduraFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenVerduraCena(alimento(verduraElegidaExtra));
        }
        //guardamos ingrediente gastado en SharedPreferences:
        sharedPrefEditorVerdura.putString("ingrediente_usado_verdura", verduraElegidaExtra);
        sharedPrefEditorVerdura.commit();
        proteinaElegidaTextView.setText(alimento(alimentoProteinas));
        if (alimentoProteinas.equals("(No hay proteinas)# $ ")) {
            imagenProteinaFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenProteinaCena(alimento(alimentoProteinas));
        }
        sharedPrefEditorProteina.putString("ingrediente_usado_proteina",alimentoProteinas);
        sharedPrefEditorProteina.commit();
        condimentoCenaElegidoTextView.setText(alimento(condimentoCena));
        if (condimentoCena.equals("(No hay condimentos)# $ ")) {
            imagenCondimentoFinal.setBackgroundResource(R.drawable.tranparente);
        } else {
            mostrarImagenCondimentoCena(alimento(condimentoCena));
        }
        sharedPrefEditorCondimentoCena.putString("ingrediente_usado_condimento",condimentoCena);
        sharedPrefEditorCondimentoCena.commit();

        peso_tiempoVerduraTextView.setText(flechas + " " + peso(verduraElegidaExtra) + "\n" + flechas + " " + tiempo(verduraElegidaExtra));
        peso_tiempoProteinaTextView.setText(flechas + " " + peso(alimentoProteinas) + "\n" + flechas + " " + tiempo(alimentoProteinas));
        peso_tiempoCondimentoCenaTextView.setText(flechas + " " + peso(condimentoCena) + "\n" + flechas + " " + tiempo(condimentoCena));


        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Mantiene pantalla encendida...

        /* para apagar la pantalla probar esto, aun no se si funciona...
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

         */

    }

    private String alimento(String textoCompleto) {
        String alimento = textoCompleto.substring(0, textoCompleto.indexOf("#"));
        return alimento;
    }

    private String peso(String textoCompleto) {
        String alimento = textoCompleto.substring(textoCompleto.indexOf("#") + 1, textoCompleto.indexOf("$"));
        return alimento;
    }

    private String tiempo(String textoCompleto) {
        String alimento = textoCompleto.substring(textoCompleto.indexOf("$") + 1, textoCompleto.length());
        return alimento;
    }


    private void mostrarImagenVerduraCena(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "judías verdes":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.judias_verdes_alimentos,null));
                break;
            case "chukrut":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.chucrut_alimentos,null));
                break;
            case "cardo":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cardo_aimentos,null));
                break;
            case "espárragos verdes":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.esparragos_alimentos,null));
                break;
            case "coles de Bruselas":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.coles_de_bruselas_alimentos,null));
                break;
            case "apio":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.apio_alimentos,null));
                break;
            case "hinojo":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hinojo_alimentos,null));
                break;
            case "borraja":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.borraja_alimentos,null));
                break;
            case "ajetes (brotes de ajo)":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ajetesalimentos,null));
                break;
            case "tallos de acelgas":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tallos_acelgas_alimentos,null));
                break;
            case "aceitunas":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.aceitunasalimentos,null));
                break;
            case "brécol":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.brecol_alimentos,null));
                break;
            case "grelos":
                imagenVerduraFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.grelos_alimentos,null));
                break;
        }
    }

    private void mostrarImagenProteinaCena(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "huevo":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.huevos_alimentos,null));
                break;
            case "champiñones":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.champinones_alimentos,null));
                break;
            case "pollo":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pollo_alimentos,null));
                break;
            case "arenques":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arenques_alimentos,null));
                break;
            case "trucha":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.trucha_alimentos,null));
                break;
            case "bonito":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bonito_alimentos,null));
                break;
            case "sardinas":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.sardinas_alimentos,null));
                break;
            case "jureles":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.jurel_alimentos,null));
                break;
            case "caballa":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.caballa_alimentos,null));
                break;
            case "atún":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.atun_alimentos,null));
                break;
            case "salmón":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.salmon_alimentos,null));
                break;
            case "boquerones":
                imagenProteinaFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.boquerones_alimentos,null));
                break;
        }
    }

    private void mostrarImagenCondimentoCena(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "vino blanco":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.vino_blanco_alimentos,null));
                break;
            case "albahaca":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.albahaca_alimentos,null));
                break;
            case "levadura de cerveza":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.levadura_de_cerveza,null));
                break;
            case "cayena":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cayena_alimentos,null));
                break;
            case "orégano":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.oregano_alimentos,null));
                break;
            case "tomillo":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tomillo_alimentos,null));
                break;
            case "curry":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.currry_alimentos,null));
                break;
            case "canela":
                imagenCondimentoFinal.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.canela_alimentos,null));
                break;
        }
    }

    public void crearNuevaComidaDesdeMFinalCena(View view) {
        Intent intencion = new Intent(this, MainActivity.class);
        startActivity(intencion);
    }

    public void verUltimoAlmuerzoDesdeMFCena(View view) {
        if(cerealSharedPreferences.contains("ingrediente_usado_cereal")){
            cerealElegido=cerealSharedPreferences.getString("ingrediente_usado_cereal","");
            legumbreElegida=legumbreSharedPreferences.getString("ingrediente_usado_legumbre","");
            hortalizaTuberculoElegido=hortalizasTuberculosSharedPreferences.getString("ingrediente_usado_hortaliza","");
            condimento_hidratosElegido=condimentoHidratosSharedPreferences.getString("ingrediente_usado_condimento","");
            Intent intencion = new Intent(this, MenuAlmuerzoFinal.class);
            intencion.putExtra("cerealElegidoExtra", cerealElegido);
            intencion.putExtra("legumbreElegidaExtra", legumbreElegida);
            intencion.putExtra("hortalizaTuberculoElegidoExtra", hortalizaTuberculoElegido);
            intencion.putExtra("condimentoHidratosElegidoExtra", condimento_hidratosElegido);
            startActivity(intencion);
        }else{
            Toast.makeText(this,"aun no hay último almuerzo...",Toast.LENGTH_LONG).show();

        }
    }
}

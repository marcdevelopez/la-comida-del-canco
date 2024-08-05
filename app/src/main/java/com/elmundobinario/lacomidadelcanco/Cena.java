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

public class Cena extends AppCompatActivity {

    ImageView imagenCenaButton;

    TextView preguntaIngrediente;

    // Base de datos con SharedPreferences:
    SharedPreferences verdurasCenaSharedPref;
    SharedPreferences tipoProteinaSharedPref;
    SharedPreferences pescadosSharedPref;
    SharedPreferences condimentosCenaSharedPref;
    SharedPreferences.Editor verdurasCenaShPrEditor;
    SharedPreferences.Editor tipoProteinaShPrEditor;
    SharedPreferences.Editor pescadosShPrEditor;
    SharedPreferences.Editor condimentoCenaShPrEditor;

    //Aquí cargarán provisionalmente los arrays:
    String[] ordenDeVerduras = {"chukrut#200 gramos.$5 minutos al vapor.",
            "judías verdes#200 gramos.$12 minutos al vapor", "cardo#200 gramos.$15 minutos al vapor",
            "espárragos verdes#200 gramos.$10 minutos al vapor.",
            "coles de Bruselas#200gramos.$10 minutos al vapor.", "apio#200 gramos.$10 minutos al vapor.",
            "hinojo#200 gramos.$10 minutos al vapor.", "borraja#200 gramos.$12 minutos al vapor.",
            "ajetes (brotes de ajo)#200 gramos.$10 minutos al vapor.",
            "tallos de acelgas#200 gramos.$10 minutos al vapor.", "aceitunas#120 gramos.$en crudo.",
            "brécol#200 gramos.$10 minutos al vapor.", "grelos#200 gramos.$12 minutos al vapor."};
    String[] ordenDeTipoDeProteinas = {"huevo#De 3 a 6 claras y 1 yema y media.$15 minutos.", "pescado# $ ",
            "champiñones#200 gramos.$10 minutos al vapor.",
            "pollo#300 gramos o 3 filetes.$23 minutos a fuego lento."};
    String[] ordenDePescados = {"arenques#240g ó 2 latas.$No hace falta calentarlo.",
            "trucha#300 gramos pesados en crudo.$19 minutos a fuego lento.",
            "bonito#De 200 a 300 gramos pesados en crudo.$De lata (o 19 minutos fuego lento).",
            "sardinas#De 200 a 300 gramos.$De lata (o 25 minutos fuego lento).",
            "jureles#De 300 a 500 gramos pesados en crudo.$25 minutos a fuego lento.",
            "caballa#O dos latas, o una caballa entera.$De lata (o 19 minutos fuego lento)",
            "atún#3 latas pequeñas.$En crudo.", "salmón#300 gramos pesados en crudo.$23 minutos fuego lento.",
            "boquerones#De 300 a 500 gramos pesados en crudo.$20 minutos."};
    String[] ordenDeCondimentosCena = {"vino blanco#Un chorreoncito bueno.$A la sartén.",
            "albahaca#Media cucharilla.$A la sartén.", "levadura de cerveza#2 cucharadas.$En crudo.",
            "cayena#La punta de una cucharilla.$En crudo.", "orégano#Media cucharilla.$A la sartén.",
            "tomillo#Media cucharilla.$A la sartén.", "curry#Media cucharilla.$En crudo.",
            "canela#Media cucharilla.$En crudo."};

    int verduraPreguntada = 0;
    int tipoProteinaPreguntada = 0;
    int pescadoPreguntado = 0;
    int condimentoCenaPreguntado = 0;

    String verduraElegida;
    String proteinaElegida;
    String condimentoCenaElegido;
    int ordenBloqueAlimento = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cena);

        imagenCenaButton = findViewById(R.id.imagen_seleccion_cena_buttonid);

        preguntaIngrediente = findViewById(R.id.ingrediente_cena_textview);

        // inicia objetos SharedPreferences:
        // en este primer objeto las verduras tienen key en indice y en valor el nombre del alimento
        // en tipoProteina es a la inversa...
        verdurasCenaSharedPref = getSharedPreferences("verduras_cena", Context.MODE_PRIVATE);
        tipoProteinaSharedPref = getSharedPreferences("tipo_proteina", Context.MODE_PRIVATE);
        pescadosSharedPref = getSharedPreferences("pescados", Context.MODE_PRIVATE);
        condimentosCenaSharedPref = getSharedPreferences("condimentos_cena", Context.MODE_PRIVATE);

        verdurasCenaShPrEditor = verdurasCenaSharedPref.edit();
        tipoProteinaShPrEditor = tipoProteinaSharedPref.edit();
        pescadosShPrEditor = pescadosSharedPref.edit();
        condimentoCenaShPrEditor = condimentosCenaSharedPref.edit();

        // SE inicializan para evitar error al lanzar MenuFinal:
        verduraElegida = "(No hay verduras)# $ ";
        proteinaElegida = "(No hay proteinas)# $ ";
        condimentoCenaElegido = "(No hay condimentos)# $ ";

        // Carga del array ordenDeVerduras si no es la primera vez... :
        if (verdurasCenaSharedPref.contains("0")) {
            for (int i = 0; i < 13; i++) {
                ordenDeVerduras[i] = verdurasCenaSharedPref.getString(String.valueOf(i), "");
            }
        }
        // Carga del array ordenDeTipoDeProteinas si no es la primera vez... :
        if (tipoProteinaSharedPref.contains("0")) {
            for (int i = 0; i < 4; i++) {
                ordenDeTipoDeProteinas[i] = tipoProteinaSharedPref.getString(String.valueOf(i), "");
            }
        }
        // Carga del array ordenDePescados si no es la primera vez... :
        if (pescadosSharedPref.contains("0")) {
            for (int i = 0; i < 9; i++) {
                ordenDePescados[i] = pescadosSharedPref.getString(String.valueOf(i), "");
            }
        }
        // Carga del array ordenDeCondimentosCena si no es la primera vez... :
        if (condimentosCenaSharedPref.contains(String.valueOf(0))) {
            for (int i = 0; i < 8; i++) {
                ordenDeCondimentosCena[i] = condimentosCenaSharedPref.getString(String.valueOf(i), "");
            }
        }

        // primera vez que muestra ingrediente preguntado. Las demás veces en onClicks...
        if (ordenBloqueAlimento == 1) {
            preguntaIngrediente.setText(alimento(ordenDeVerduras[verduraPreguntada]) + "?");
            mostrarImagenSeleccionAlimentoCena(alimento(ordenDeVerduras[verduraPreguntada]));
        }

    }

    public void clickSi_Cena(View view) {
        switch (ordenBloqueAlimento) {
            case 1:
                // 1 = VERDURAS:
                verduraElegida = ordenDeVerduras[verduraPreguntada];
                rotarArrayAlimento();
                ordenBloqueAlimento++;
                if (alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]).equals("pescado")) {
                    preguntaIngrediente.setText(alimento(ordenDePescados[pescadoPreguntado]) + "?");
                    mostrarImagenSeleccionAlimentoCena(alimento(ordenDePescados[pescadoPreguntado]));
                } else {
                    preguntaIngrediente.setText(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]) + "?");
                    mostrarImagenSeleccionAlimentoCena(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]));
                }
                verduraPreguntada = 0;
                break;
            case 2:
                // 2 = tipo de PROTEINA:
                if (alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]).equals("pescado")) {
                    proteinaElegida = ordenDePescados[pescadoPreguntado]; // este será el String que se
                    //muestre en MenuAlmuerzoFinal
                } else {
                    proteinaElegida = ordenDeTipoDeProteinas[tipoProteinaPreguntada];
                }
                rotarArrayAlimento();
                ordenBloqueAlimento++;
                preguntaIngrediente.setText(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]) + "?");
                mostrarImagenSeleccionAlimentoCena(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]));
                tipoProteinaPreguntada = 0;
                break;
            case 3:
                // 3 = condimento cena:
                condimentoCenaElegido = ordenDeCondimentosCena[condimentoCenaPreguntado];
                rotarArrayAlimento();
                // Aqui ya se tienen todos los alimentos
                // AQUI VA EL DÓDIGO PARA GUARDAR DATOS DE ARRAYS EN ARCHIVOS
                guardarOrdenVerduras();
                guardarOrdenTipoProteinas();
                guardarOrdenPescados();
                guardarOrdenCondimentosCena();
                condimentoCenaPreguntado = 0;
                lanzaActivityMenuFinal();
                break;
        }

    }

    public void clickNo_Cena(View view) {
        switch (ordenBloqueAlimento) {
            // si es una VERDURA:
            case 1:
                verduraPreguntada++;
                if (verduraPreguntada >= 13) { // ya no hay más verduras, pasa a proteinas...
                    ordenBloqueAlimento++;
                    if (alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]).equals("pescado")) {
                        if (pescadoPreguntado >= 9) { // ya no hay más pescados en la lista...
                            tipoProteinaPreguntada++; // se pteguntará el siguiente tipo de pescado...
                        } else {
                            preguntaIngrediente.setText(alimento(ordenDePescados[pescadoPreguntado]) + "?");
                            mostrarImagenSeleccionAlimentoCena(alimento(ordenDePescados[pescadoPreguntado]));
                        }
                    } else { // solo pregunta el siguiente tipoProteina, y pasa al bloque 2 de proteinas...
                        preguntaIngrediente.setText(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]) + "?");
                        mostrarImagenSeleccionAlimentoCena(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]));
                    }
                    verduraPreguntada = 0;
                } else { // Si aun quedan verduras la preguntara:
                    preguntaIngrediente.setText(alimento(ordenDeVerduras[verduraPreguntada]) + "?");
                    mostrarImagenSeleccionAlimentoCena(alimento(ordenDeVerduras[verduraPreguntada]));
                }
                break;
            // si es un tipo de PROTEINA:
            case 2:
                if (alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]).equals("pescado")) {
                    pescadoPreguntado++;
                    if (pescadoPreguntado >= 9) { // ya no hay más pescados en la lista...
                        tipoProteinaPreguntada++;
                        if (tipoProteinaPreguntada >= 4) {
                            ordenBloqueAlimento++;
                            preguntaIngrediente.setText(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]) + "?");
                            mostrarImagenSeleccionAlimentoCena(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]));
                        } else {
                            preguntaIngrediente.setText(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]) + "?");
                            mostrarImagenSeleccionAlimentoCena(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]));
                        }
                    } else {
                        preguntaIngrediente.setText(alimento(ordenDePescados[pescadoPreguntado]) + "?");
                        mostrarImagenSeleccionAlimentoCena(alimento(ordenDePescados[pescadoPreguntado]));
                    }
                } else {
                    tipoProteinaPreguntada++;
                    if (tipoProteinaPreguntada >= 4) { // Si ya no hay maś tipos de proteinas por preguntar:
                        ordenBloqueAlimento++;
                        preguntaIngrediente.setText(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]) + "?");
                        mostrarImagenSeleccionAlimentoCena(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]));
                    } // aun quedan tipos de proteinas por preguntar:
                    else {
                        if (alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]).equals("pescado")) {
                            preguntaIngrediente.setText(alimento(ordenDePescados[pescadoPreguntado]) + "?");
                            mostrarImagenSeleccionAlimentoCena(alimento(ordenDePescados[pescadoPreguntado]));
                        } else {
                            preguntaIngrediente.setText(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]) + "?");
                            mostrarImagenSeleccionAlimentoCena(alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]));
                        }
                    }
                }
                break;
            // si es un CONDIMENTO:
            case 3:
                condimentoCenaPreguntado++;
                if (condimentoCenaPreguntado >= 8) { // ya no hay más condimentos
                    // Aqui ya no hay más alimentos por preguntar...
                    // aqui se guardarán los datos de los arrays a los SharedPreferences:
                    guardarOrdenVerduras();
                    guardarOrdenTipoProteinas();
                    guardarOrdenPescados();
                    guardarOrdenCondimentosCena();
                    condimentoCenaPreguntado = 0;
                    lanzaActivityMenuFinal();
                } else {
                    preguntaIngrediente.setText(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]) + "?");
                    mostrarImagenSeleccionAlimentoCena(alimento(ordenDeCondimentosCena[condimentoCenaPreguntado]));
                }
                break;
        }

    }

    private void rotarArrayAlimento() {
        // ROTAR VERDURAS:
        if (ordenBloqueAlimento == 1) {
            String reordenandoVerduras[] = new String[13];
            reordenandoVerduras[12] = ordenDeVerduras[verduraPreguntada];
            int siguienteVerduraPreguntadaEnIf = 0;
            for (int i = 0; i < 12; i++) {
                if (i == verduraPreguntada)
                    siguienteVerduraPreguntadaEnIf++;// salta el indice vacio
                // del indice que se mueve al final (ultimo indice del array)
                reordenandoVerduras[i] = ordenDeVerduras[siguienteVerduraPreguntadaEnIf];
                siguienteVerduraPreguntadaEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 13; i++) {
                ordenDeVerduras[i] = reordenandoVerduras[i];
            }
        }
        // ROTAR TIPO DE PROTEINAS y pescados...
        else if (ordenBloqueAlimento == 2) {
            String reordenandoTipoDeProteinas[] = new String[4];
            reordenandoTipoDeProteinas[3] = ordenDeTipoDeProteinas[tipoProteinaPreguntada];
            int siguienteTipoProteinaPreguntadaEnIf = 0;
            for (int i = 0; i < 3; i++) {
                if (i == tipoProteinaPreguntada) siguienteTipoProteinaPreguntadaEnIf++;
                reordenandoTipoDeProteinas[i] = ordenDeTipoDeProteinas[siguienteTipoProteinaPreguntadaEnIf];
                siguienteTipoProteinaPreguntadaEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 4; i++) {
                ordenDeTipoDeProteinas[i] = reordenandoTipoDeProteinas[i];
            }
            //aqui dentro tambien se ordenara el array de PESCADOS si es el caso de ser elegido...
            if (alimento(ordenDeTipoDeProteinas[tipoProteinaPreguntada]).equals("pescado")) {
                String reordenandoPescados[] = new String[9];
                reordenandoPescados[8] = ordenDePescados[pescadoPreguntado];
                int siguientePescadoPreguntadoEnIf = 0;
                for (int i = 0; i < 8; i++) {
                    if (i == pescadoPreguntado) siguientePescadoPreguntadoEnIf++;
                    reordenandoPescados[i] = ordenDePescados[siguientePescadoPreguntadoEnIf];
                    siguientePescadoPreguntadoEnIf++;
                }
                // Pasamos el array de seguridad al array original, actualizando así el array:
                for (int i = 0; i < 9; i++) {
                    ordenDePescados[i] = reordenandoPescados[i];
                }
            }
        }
        // ROTAR CONDIMENTO CENA:
        else {
            String reordenandoCondimentosCena[] = new String[8];
            // se pone en ultimo lugar el condimento elegido:
            reordenandoCondimentosCena[7] = ordenDeCondimentosCena[condimentoCenaPreguntado];
            int siguienteCondimentoCenaPreguntadoEnIf = 0; // (variable para saltar el indice vacio por el
            // indice elegido y puesto el ultimo)
            for (int i = 0; i < 7; i++) {
                if (i == condimentoCenaPreguntado) siguienteCondimentoCenaPreguntadoEnIf++;
                reordenandoCondimentosCena[i] = ordenDeCondimentosCena[siguienteCondimentoCenaPreguntadoEnIf];
                siguienteCondimentoCenaPreguntadoEnIf++;
            }
            // Pasamos el array de seguridad al array original, actualizando así el array:
            for (int i = 0; i < 8; i++) {
                ordenDeCondimentosCena[i] = reordenandoCondimentosCena[i];
            }
        }

    }

    private void guardarOrdenVerduras() {
        for (int i = 0; i < 13; i++) {
            verdurasCenaShPrEditor.putString(String.valueOf(i), ordenDeVerduras[i]);
            verdurasCenaShPrEditor.commit();
        }
    }

    private void guardarOrdenTipoProteinas() {
        for (int i = 0; i < 4; i++) {
            tipoProteinaShPrEditor.putString(String.valueOf(i), ordenDeTipoDeProteinas[i]);
            tipoProteinaShPrEditor.commit();
        }
    }

    private void guardarOrdenPescados() {
        for (int i = 0; i < 9; i++) {
            pescadosShPrEditor.putString(String.valueOf(i), ordenDePescados[i]);
            pescadosShPrEditor.commit();
        }
    }

    private void guardarOrdenCondimentosCena() {
        for (int i = 0; i < 8; i++) {
            condimentoCenaShPrEditor.putString(String.valueOf(i), ordenDeCondimentosCena[i]);
            condimentoCenaShPrEditor.commit();
        }
    }

    public String alimento(String textoCompleto) {
        String alimento = textoCompleto.substring(0, textoCompleto.indexOf("#"));
        return alimento;
    }

    public void mostrarImagenSeleccionAlimentoCena(String alimentoAMostrar) {
        switch (alimentoAMostrar) {
            case "judías verdes":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.judias_verdes_alimentos,null));
                break;
            case "chukrut":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.chucrut_alimentos,null));
                break;
            case "cardo":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cardo_aimentos,null));
                break;
            case "espárragos verdes":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.esparragos_alimentos,null));
                break;
            case "coles de Bruselas":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.coles_de_bruselas_alimentos,null));
                break;
            case "apio":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.apio_alimentos,null));
                break;
            case "hinojo":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hinojo_alimentos,null));
                break;
            case "borraja":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.borraja_alimentos,null));
                break;
            case "ajetes (brotes de ajo)":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ajetesalimentos,null));
                break;
            case "tallos de acelgas":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tallos_acelgas_alimentos,null));
                break;
            case "aceitunas":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.aceitunasalimentos,null));
                break;
            case "brécol":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.brecol_alimentos,null));
                break;
            case "grelos":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.grelos_alimentos,null));
                break;
            case "huevo":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.huevos_alimentos,null));
                break;
            case "champiñones":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.champinones_alimentos,null));
                break;
            case "pollo":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pollo_alimentos,null));
                break;
            case "arenques":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.arenques_alimentos,null));
                break;
            case "trucha":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.trucha_alimentos,null));
                break;
            case "bonito":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bonito_alimentos,null));
                break;
            case "sardinas":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.sardinas_alimentos,null));
                break;
            case "jureles":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.jurel_alimentos,null));
                break;
            case "caballa":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.caballa_alimentos,null));
                break;
            case "atún":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.atun_alimentos,null));
                break;
            case "salmón":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.salmon_alimentos,null));
                break;
            case "boquerones":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.boquerones_alimentos,null));
                break;
            case "vino blanco":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.vino_blanco_alimentos,null));
                break;
            case "albahaca":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.albahaca_alimentos,null));
                break;
            case "levadura de cerveza":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.levadura_de_cerveza,null));
                break;
            case "cayena":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.cayena_alimentos,null));
                break;
            case "orégano":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.oregano_alimentos,null));
                break;
            case "tomillo":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tomillo_alimentos,null));
                break;
            case "curry":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.currry_alimentos,null));
                break;
            case "canela":
                imagenCenaButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.canela_alimentos,null));
                break;
        }
    }

    private void lanzaActivityMenuFinal() {
        Intent intencion = new Intent(this, MenuCenaFinal.class);
        intencion.putExtra("verduraElegidaExtra", verduraElegida);
        intencion.putExtra("alimentoProteinas", proteinaElegida);
        intencion.putExtra("condimentoCena", condimentoCenaElegido);
        startActivity(intencion);
    }
}

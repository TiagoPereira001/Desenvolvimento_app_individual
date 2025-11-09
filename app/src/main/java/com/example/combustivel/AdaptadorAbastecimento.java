package com.example.combustivel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Adaptador  para o RecyclerView de Abastecimentos.
public class AdaptadorAbastecimento extends RecyclerView.Adapter<AdaptadorAbastecimento.AbastecimentoViewHolder> {

    // A lista de dados que o adaptador vai mostrar
    private List<Abastecimento> abastecimentos;

    // Um formatador para transformar a data
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Recebe a lista inicial de abastecimentos quando e criado.
    public AdaptadorAbastecimento(List<Abastecimento> abastecimentos) {
        this.abastecimentos = abastecimentos;
    }

    // Metodo publico para atualizar a lista de dados.
    public void atualizarLista(List<Abastecimento> novaLista) {
        this.abastecimentos = novaLista;
        // Avisa o RecyclerView que os dados mudaram
        notifyDataSetChanged();
    }

     // Metodo  (Override) do RecyclerView
    @NonNull
    @Override
    public AbastecimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Inflar" (carregar) o layout XML (o nosso "cartao")
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_abastecimento, parent, false);

        // Devolver o ViewHolder com o layout la dentro
        return new AbastecimentoViewHolder(view);
    }

    // Metodo (Override) do RecyclerView é chamado para "popular" (preencher) um "cartao"


    @Override
    public void onBindViewHolder(@NonNull AbastecimentoViewHolder holder, int position) {
        // 1. Ir buscar o abastecimento correto da lista
        Abastecimento abastecimento = abastecimentos.get(position);

        // 2. Chamar o metodo 'bind' do ViewHolder para preencher os TextViews
        holder.bind(abastecimento, dateFormat);
    }

    //Metodo  (Override) do RecyclerView. Diz ao RecyclerView quantos items existem no total


    @Override
    public int getItemCount() {
        return abastecimentos.size();
    }

    // Classe interna (ViewHolder). Representa UM "cartao" (um item) na nossa lista.
    static class AbastecimentoViewHolder extends RecyclerView.ViewHolder {


        private final TextView tvData;
        private final TextView tvCusto;
        private final TextView tvKms;
        private final TextView tvLitros;

        // Construtor do ViewHolder. E aqui que fazemos a ligacao (findViewById) entre as variaveis java e os ids do xml

        AbastecimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ligar as variaveis aos IDs
            tvData = itemView.findViewById(R.id.item_data);
            tvCusto = itemView.findViewById(R.id.item_custo_total);
            tvKms = itemView.findViewById(R.id.item_kms);
            tvLitros = itemView.findViewById(R.id.item_litros);
        }

        // Metodo " ajudante  que preenche os TextViews é chamado pelo 'onBindViewHolder'.


        public void bind(final Abastecimento ab, SimpleDateFormat dateFormat) {
            // Formatar todos os dados para Strings legiveis
            String dataFormatada = dateFormat.format(new Date(ab.data));

            String custoFormatado = String.format(Locale.getDefault(), "%.2f €", ab.custoTotal);

            String kmsFormatado = String.format(Locale.getDefault(), "%.1f km", ab.kilometros);

            String litrosFormatado = String.format(Locale.getDefault(), "%.1f L", ab.litros);

            // Colocar as strings formatadas nos TextViews
            tvData.setText(dataFormatada);
            tvCusto.setText(custoFormatado);
            tvKms.setText(kmsFormatado);
            tvLitros.setText(litrosFormatado);
        }
    }
}
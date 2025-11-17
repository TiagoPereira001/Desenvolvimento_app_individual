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

public class AdaptadorAbastecimento extends RecyclerView.Adapter<AdaptadorAbastecimento.AbastecimentoViewHolder> {

    private List<Abastecimento> abastecimentos;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final OnAbastecimentoLongClickListener longClickListener;
    private String tipoVeiculo;

    public interface OnAbastecimentoLongClickListener {
        void onAbastecimentoLongClicked(Abastecimento abastecimento);
    }

    public AdaptadorAbastecimento(List<Abastecimento> abastecimentos, String tipoVeiculo, OnAbastecimentoLongClickListener listener) {
        this.abastecimentos = abastecimentos;
        this.tipoVeiculo = tipoVeiculo;
        this.longClickListener = listener;
    }

    public void atualizarLista(List<Abastecimento> novaLista) {
        this.abastecimentos = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AbastecimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_abastecimento, parent, false);
        return new AbastecimentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbastecimentoViewHolder holder, int position) {
        Abastecimento abastecimento = abastecimentos.get(position);
        holder.bind(abastecimento, dateFormat, tipoVeiculo, longClickListener);
    }

    @Override
    public int getItemCount() {
        return abastecimentos.size();
    }

    static class AbastecimentoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvData, tvCusto, tvKms, tvLitros;

        AbastecimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.item_data);
            tvCusto = itemView.findViewById(R.id.item_custo_total);
            tvKms = itemView.findViewById(R.id.item_kms);
            tvLitros = itemView.findViewById(R.id.item_litros);
        }

        public void bind(final Abastecimento ab, SimpleDateFormat dateFormat, String tipo, final OnAbastecimentoLongClickListener listener) {

            String dataFormatada = dateFormat.format(new Date(ab.data));
            String custoFormatado = String.format(Locale.getDefault(), "%.2f €", ab.custoTotal);
            String kmsFormatado = String.format(Locale.getDefault(), "%.1f km", ab.kilometros);

            String unidadeFormatada;
            if (tipo != null && tipo.equals("ELETRICO")) {
                unidadeFormatada = String.format(Locale.getDefault(), "%.1f kWh", ab.litros);
            } else {
                unidadeFormatada = String.format(Locale.getDefault(), "%.1f L", ab.litros);
            }

            tvData.setText(dataFormatada);
            tvCusto.setText(custoFormatado);
            tvKms.setText(kmsFormatado);
            tvLitros.setText(unidadeFormatada);

            itemView.setOnLongClickListener(v -> {
                listener.onAbastecimentoLongClicked(ab);
                return true;
            });
        }
    }
}
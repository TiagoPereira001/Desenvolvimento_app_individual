package com.example.combustivel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdaptadorVeiculo extends RecyclerView.Adapter<AdaptadorVeiculo.VeiculoViewHolder> {

    private List<Veiculo> veiculos;
    private final OnVeiculoClickListener listener;

    public interface OnVeiculoClickListener {
        void onVeiculoClick(Veiculo veiculo);
    }

    public AdaptadorVeiculo(List<Veiculo> veiculos, OnVeiculoClickListener listener) {
        this.veiculos = veiculos;
        this.listener = listener;
    }

    public void atualizarLista(List<Veiculo> novaLista) {
        this.veiculos = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VeiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_veiculo, parent, false);
        return new VeiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VeiculoViewHolder holder, int position) {
        Veiculo veiculo = veiculos.get(position);
        holder.bind(veiculo, listener);
    }

    @Override
    public int getItemCount() {
        return veiculos.size();
    }

    static class VeiculoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNome;
        private final TextView tvDesc;

        VeiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.item_veiculo_nome);
            tvDesc = itemView.findViewById(R.id.item_veiculo_desc);
        }

        public void bind(final Veiculo veiculo, final OnVeiculoClickListener listener) {
            tvNome.setText(veiculo.getNome());
            tvDesc.setText(veiculo.getMarca() + " " + veiculo.getModelo());

            itemView.setOnClickListener(v -> {
                listener.onVeiculoClick(veiculo);
            });
        }
    }
}
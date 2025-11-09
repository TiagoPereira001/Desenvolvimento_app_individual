package com.example.combustivel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

    // adpatador que liga ao recycler view de veiculos, faz a ponte entre a fonte de dados e o recycler view
public class AdaptadorVeiculo extends RecyclerView.Adapter<AdaptadorVeiculo.VeiculoViewHolder> {

    // A fonte de dados que o adaptador vai mostrar.
    private List<Veiculo> veiculos;

    // Variavel para guardar a interface de clique (o 'ouvinte'),Esta permite-nos enviar o clique de volta para a MainActivity.
    private final OnVeiculoClickListener listener;

    // Interface de retorno, com isto a main activity vai implementar esta interface para saber qual e quando é que o veiculo foi criado
    public interface OnVeiculoClickListener {
        void onVeiculoClick(Veiculo veiculo);
    }

    // Este contutor recebe a lista inicial de veiculos e o listener que vem da main activity

    public AdaptadorVeiculo(List<Veiculo> veiculos, OnVeiculoClickListener listener) {
        this.veiculos = veiculos;
        this.listener = listener;
    }

    // Atualiza a lista de dados, o "onResume" chama este metodo quando os dados tem que ser recarregados

    public void atualizarLista(List<Veiculo> novaLista) {
        // Substitui a lista antiga pela nova.
        this.veiculos = novaLista;

        //Avisa o RecyclerView que os dados mudaram
        notifyDataSetChanged();
    }

    // Agora chama pelo Recycler view quando precisas de criar um molde novo para um item da lista

    @NonNull
    @Override
    public VeiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Inflar" (carregar/converter) o nosso layout XML (item_veiculo.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_veiculo, parent, false);

        // Criar e devolver um novo ViewHolder, passando a view do "cartao".
        return new VeiculoViewHolder(view);
    }

    // Chamado pelo recycler view para preencher um cartao que ja existe com os dados de um veiculo

    @Override
    public void onBindViewHolder(@NonNull VeiculoViewHolder holder, int position) {
        // Vai buscar o objeto 'Veiculo' especifico desta posicao na lista.
        Veiculo veiculo = veiculos.get(position);

        // 2. Chamar o nosso metodo 'bind' (definido no ViewHolder) para preencher os "TextViews" com os dados do veiculo.
        holder.bind(veiculo, listener);
    }

    // Diz ao Reyclerview quantos "item" existem na lista

    @Override
    public int getItemCount() {
        // Se a lista for nula, devolve 0, senao, devolve o tamanho.
        return veiculos != null ? veiculos.size() : 0;
    }

    // Classe para representar um "item" na lista, guarda as referencias para o textview

    static class VeiculoViewHolder extends RecyclerView.ViewHolder {

        // Variaveis para guardar as referencias aos TextViews do XML, (sao so atribuidas uma vez por isso o "final")
        private final TextView tvNome;
        private final TextView tvDesc;

        // construtor que faz a ligação entre as variaveis do java e o xml

        VeiculoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Fazer o 'findViewById' e guarda as referencias.
            tvNome = itemView.findViewById(R.id.item_veiculo_nome);
            tvDesc = itemView.findViewById(R.id.item_veiculo_desc);
        }

        // Metodo para ligar os dados ao cartao.

        public void bind(final Veiculo veiculo, final OnVeiculoClickListener listener) {
            // Preenche o TextView do nome com o nome do veiculo.
            tvNome.setText(veiculo.getNome());

            // Liga a marca e o modelo para a descricao.
            tvDesc.setText(veiculo.getMarca() + " " + veiculo.getModelo());

            // Configura o clique no cartao
            itemView.setOnClickListener(v -> {
                // Quando clicado, chama o metodo onVeiculoClick da interface do listener
                listener.onVeiculoClick(veiculo);
            });
        }
    }
}
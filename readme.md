# Desafio Conductor

## Execução

Para executar o projeto, basta importa-lo no Android Studio, escolher um das duas Build Variantes (Flavors),
Green Card ou Blue Card, e rodar, seja em um emulador ou dispositivo ligado ao USB.

## O que foi feito

1. Flavors
   Foram construídos duas variantes para essa aplicação. Uma com tema verde para o Green Card, outro com o tema azul
   o Blue Card.
2. Extrato
   Foi implementado um Recycler View para o demonstrativo de compras do mês atual. O mês e ano atual será pego do SO
   e os dados serão trazidos do servidor, como especificado. O Recycler View carregará uma página por vez, ao término
   do scrolling, outra página será carregada até que não sobrem mais páginas.
3. Gráfico de Compras Mensal
   Foi implementado usando a biblioteca MPAndroidChart. Os dados do gastos mês a mês são carregados do servidor e
   plotados em um gráfico de barras com design de acordo com o mockup.
4. Dados do Cardholder
   O design foi implementado usando uma cominação de layouts e drawables (XML) no Android Studio. O dados do Cardholder
   são trazidor do servidor e apresentados na frente do cartão.

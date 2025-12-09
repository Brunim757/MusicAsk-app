🌐 README – MusicAsk Site (web)

# MusicAsk Site 🎵🌐

O **MusicAsk Site** é a parte web do sistema MusicAsk.  
É a plataforma onde o público pode:

- Enviar pedidos de música
- Avaliar o DJ ou evento
- Escanear um QR Code para acessar a página de pedidos
- Interagir sem precisar instalar nada

O site se conecta diretamente ao MusicAsk App via APIs.

---

## 📌 Funcionalidades

- Página para envio de pedidos
- Página para avaliações
- QR Code fixo para facilitar o acesso durante eventos
- Painel do evento (opcional)
- Ativação/desativação de recebimento de pedidos (opcional)
- Sistema totalmente open-source e adaptável

---

## 🔌 APIs obrigatórias

Para ser compatível com o **MusicAsk App**, o site deve implementar:

### Pedidos
#### `POST /api/request`
Cria um pedido de música.  
Exemplo de payload:
```json
{
  "name": "Nome da pessoa",
  "music": "Nome da música"
}

GET /api/requests

Lista todos os pedidos existentes.


---

Avaliações

POST /api/rating

Envia uma avaliação.
Exemplo:

{
  "stars": 5,
  "comment": "Muito bom!"
}

GET /api/ratings

Retorna todas as avaliações.


---

📐 Estrutura recomendada do projeto

pages/
  api/
    request.js
    requests.js
    rating.js
    ratings.js
  index.js (página de pedidos)
  rate.js (página de avaliação)
utils/
public/


---

🔒 Segurança recomendada (opcional)

Bloquear pedidos quando não estiver em evento

Ativar “modo evento” via token temporário

Gerar QR Code que libera somente a página de pedidos

Limitar pedidos por tempo/IP



---

🧩 Integração com o app

O app conecta através da URL base fornecida pelo usuário.

Exemplo:

Base: https://meusite.com

GET  https://meusite.com/api/requests
POST https://meusite.com/api/request


---

🤝 Contribuições

1. Faça um fork


2. Crie uma branch


3. Commit


4. Abra um Pull Request




---

📄 Licença

Licença MIT — totalmente livre.


---

💡 Sobre

Criado como ferramenta simples para eventos e DJs.
Aberto ao público para que qualquer pessoa possa personalizar e utilizar em festas, igrejas, shows e eventos em geral.

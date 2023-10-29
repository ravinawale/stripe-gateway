// Fetch price data.
const pricesDiv = document.querySelector('#price-list');

fetch('/config')
  .then((response) => response.json())
  .then((data) => {
    pricesDiv.innerHTML = '';
    if(!data.prices) {
      pricesDiv.innerHTML = `
        <h3>No prices found</h3>
        <p>This sample requires two prices, one with the lookup_key sample_basic and another with the lookup_key sample_premium</p>
        <p>You can create these through the API or with the Stripe CLI using the provided seed.json fixture file with: <code>stripe fixtures seed.json</code>
      `
    }
    data.prices.forEach((price) => {
      pricesDiv.innerHTML += `
        <div>
          product : ${price.product.id} <br/><br>
          Subscription : ${price.unit_amount / 100} INR / ${price.recurring.interval} <br><br>
          <button onclick="createSubscription('${price.id}')">Select</button> <br/>
        </div>
      `;
    });
  })
  .catch((error) => {
    console.error('Error:', error);
  });


const createSubscription = (priceId) => {
	  return fetch('/create-subscription', {
	    method: 'POST',
	    headers: {
	      'Content-Type': 'application/json',
	      'customerId':window.sessionStorage.getItem('customerId')
	    },
	    body: JSON.stringify({
	      priceId: priceId,
	    }),
	  })
     .then((response) => response.json())
     .then((data) => {
	      window.sessionStorage.setItem('subscriptionId', data.subscriptionId);
	      window.sessionStorage.setItem('clientSecret', data.clientSecret);
	      window.location.href = '/subscribe.html';
     })
     .catch((error) => {
      	console.error('Error:', error);
     });
}

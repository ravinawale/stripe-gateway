document.addEventListener('DOMContentLoaded', async () => {
  // Fetch the list of subscriptions for this customer.
  const {subscriptions} = await fetch('/subscriptions', {
					       method: 'GET',
					       headers: {
					      	'Content-Type': 'application/json',
					      	'customerId':window.sessionStorage.getItem('customerId')
					   	   },
					  })
  					 .then((r) => r.json());
  // Construct and display each subscription, its status, last4 of the card
  // used, and the current period end.
  const subscriptionsDiv = document.querySelector('#subscriptions');
  subscriptionsDiv.innerHTML = subscriptions.data.map((subscription) => {
    let last4 = subscription.default_payment_method?.card?.last4 || '';
    return `
      <table class="panel2">
            <tr>
	            <td><a href="https://dashboard.stripe.com/test/subscriptions/${subscription.id}">
		            ${subscription.id}
		        	</a>
	        	</td>
	        	<td>${subscription.status}</td>
	            <td>${last4}</td>
	            <td>${new Date(subscription.current_period_end * 1000)}</td>
	            <td><a href="change-plan.html?subscription=${subscription.id}"> Change plan </a></td>
	            <td><a href="cancel.html?subscription=${subscription.id}"> Cancel </a></td>
            </tr>
      <table>
    `;
  }).join('<br />');
});

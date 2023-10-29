document.addEventListener('DOMContentLoaded', async () => {
  const signupForm = document.querySelector('#signup-form');
  if (signupForm) {
    signupForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      // Grab reference to the emailInput. The email address
      // entered will be passed to the server and used to create
      // a customer. Email addresses do NOT uniquely identify
      // customers in Stripe.
      const emailInput = document.querySelector('#email');
      const nameInput = document.querySelector('#cname');

      // Create a customer. This will also set a cookie on the server
      // to simulate having a logged in user.
     await fetch('/create-customer', {
        method: 'post',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: emailInput.value,
          cname: nameInput.value,
        }),
      })
      .then(r => r.json())
      .then((data) => {
	      window.sessionStorage.setItem('customerId', data.customerId);
      })
      // Redirect to the pricing page.
      //alert("Cust : "+customer);
      //window.sessionStorage.setItem('customerId', data.customerId);
      window.location.href = '/prices.html';
    });
  } else {
      alert("No sign up form with ID `signup-form` found on the page.");
  }
});

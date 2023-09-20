const unverifiedAlert = document.getElementById('unverified-alert');

document.addEventListener('DOMContentLoaded', () => {
  getUserInfo((data) => {
    if (!data.verified) {
      unverifiedAlert.style.display = 'block';
    } else {
      unverifiedAlert.style.display = 'none';
    }
  }, (error) => {
    console.log(error);
  });
});



<template>
  <nav class="navbar">
    <h1>Inventory management system</h1>
    <ul>
      <li><router-link to="/">Home</router-link></li>
    </ul>
  </nav>
</template>

<style scoped>
.navbar {
  background: #1c3fb1;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.navbar h1 {
  font-size: 1.4rem;
  margin: 0;
}

.navbar ul {
  list-style: none;
  display: flex;
  gap: 1.5rem;
  margin: 0;
  padding: 0;
}

.navbar li {
  font-weight: 500;
}

.navbar a {
  color: white;
  text-decoration: underline;
}

.navbar a:hover {
  opacity: 0.85;
}

@media (max-width: 600px) {
  .navbar {
    padding: 1rem;
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
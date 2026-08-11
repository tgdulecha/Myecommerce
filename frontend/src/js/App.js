import { ref } from "vue";

/* ---------- STATE ---------- */
const inserted = ref("");
const posts = ref([
  { id: 1, title: "My journey with Vue" },
  { id: 2, title: "Blogging with Vue" },
  { id: 3, title: "Why Vue is so fun" },
]);

const categories = ref([]);

/* ---------- FUNCTIONS ---------- */
async function getData() {
  const url = "http://localhost:8080/Myecommerce/api/category";

  const response = await fetch(url);
  const items = await response.json();

  categories.value = items.map(item => ({
    id: item.categoryID,
    name: item.categoryName,
    description: item.description,
  }));
}

async function saveCategory(category) {
  await fetch("http://localhost:8080/Myecommerce/api/category", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(category),
  });

  await getData(); // refresh after save
}

function viewvalues(value) {
  inserted.value = value;
}

/* ---------- EXPORT ---------- */
export function useApp() {
  return {
    inserted,
    posts,
    categories,
    getData,
    saveCategory,
    viewvalues,
  };
}

export function useCategorySave(emit) {
  const categoryName = ref("");
  const description = ref("");

  async function saveValue() {
    const product = {
      categoryName: categoryName.value,
      description: description.value,
    };

    try {
      const response = await fetch(
        "http://localhost:8080/Myecommerce/api/category",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(product),
        }
      );

      if (!response.ok) {
        throw new Error("Save failed");
      }

      emit("change", product); // notify parent
       categoryName.value = "";
      description.value = "";
    } catch (err) {
      emit("onerror", err.message);
    }
  }
  return {
    categoryName,
    description,
    saveValue,
  };
}

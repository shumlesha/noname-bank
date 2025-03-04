export function showModal() {
    let modal = document.getElementById("tariff-modal");
    modal.style.display = "flex";
    modal.classList.add("show");
}

export function closeModal() {
    let modal = document.getElementById("tariff-modal");
    modal.style.display = "none";
    modal.classList.remove("show");
}
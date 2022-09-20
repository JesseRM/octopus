function ItemRow({ id, name, stock, capacity, setOrderAmounts }) {
  const max = capacity - stock;
  
  function handleAmountChange(event) {
    const amount = parseInt(event.target.value);

    if (amount > max) {
      alert("The most you can order for this item is " + max + " or less.");
      event.target.value = 0;

      return;
    }

    setOrderAmounts(prevState => ({
      ...prevState,
      [id]: amount
    }))
  }

  return (
    <tr>
      <td>{id}</td>
      <td>{name}</td>
      <td>{stock}</td>
      <td>{capacity}</td>
      <td>
        <input 
          type="number" 
          defaultValue="0" 
          min="0"
          max={max}
          onChange={handleAmountChange} 
        />
      </td>
    </tr>
  )
}

export default ItemRow;
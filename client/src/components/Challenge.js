import { useState } from "react";
import ItemRow from "./ItemRow";

export default function Challenge() {
  const [orderAmounts, setOrderAmounts] = useState({});
  const [lowStock, setLowStock] = useState([]);
  
  function handleLowStockClick() {
    fetch("http://localhost:4567/low-stock")
      .then((response) => response.json())
      .then((data) => {
        setLowStock(data);
      });
  }

  return (
    <>
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount</td>
          </tr>
        </thead>
        <tbody>
          {/* 
          TODO: Create an <ItemRow /> component that's rendered for every inventory item. The component
          will need an input element in the Order Amount column that will take in the order amount and 
          update the application state appropriately.
          */

            lowStock.map((item, index) => {
              return(
                <ItemRow
                  key={index}
                  id={item.id}
                  name={item.name}
                  stock={item.stock}
                  capacity={item.capacity}
                  setOrderAmounts={setOrderAmounts}
                />
              )
            })
          }
        </tbody>
      </table>
      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: </div>
      {/* 
      TODO: Add event handlers to these buttons that use the Java API to perform their relative actions.
      */}
      <button onClick={handleLowStockClick}>Get Low-Stock Items</button>
      <button>Determine Re-Order Cost</button>
    </>
  );
}

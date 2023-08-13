import { useEffect, useState } from "react";
import axios from "axios";

function App() {
  const [state, setState] = useState({
    users: [],
    id: 0,
    name: "",
    email: "",
    password: "",
  });

  function loadUser() {
    axios.get("http://localhost:8080/api/").then((res) => {
      setState({
        users: res.data,
        id: 0,
        name: "",
        email: "",
        password: "",
      });
    });
  }

  useEffect(() => {
    //debugger;
    axios.get("http://localhost:8080/api/").then((res) => {
      setState({
        users: res.data,
        id: 0,
        name: "",
        email: "",
        password: "",
      });
    });

    console.log("user data test", state.users);
  }, []);

  function onSubmit(event, id) {
    event.preventDefault();
    if (id === 0) {
      axios
        .post("http://localhost:8080/api/", {
          name: state.name,
          email: state.email,
          password: state.password,
        })
        .then((res) => {
          loadUser();
        });
    } else {
      axios
        .put("http://localhost:8080/api/", {
          id: state.id,
          name: state.name,
          email: state.email,
          password: state.password,
        })
        .then(() => {
          loadUser();
        });
    }
  }

  function onDelete(id) {
    axios.delete(`http://localhost:8080/api/${id}`).then(() => {
      loadUser();
    });
  }

  function onEdit(id) {
    axios.get(`http://localhost:8080/api/${id}`).then((res) => {
      console.log(res.data);
      this.setState({
        id: res.data.id,
        name: res.data.name,
        email: res.data.email,
        password: res.data.password,
      });
    });
  }

  return (
    <div className="container">
      <div className="row">
        <div className="col s6">
          <form onSubmit={(e) => onSubmit(e, state.id)}>
            <div class="input-field col s12">
              <i class="material-icons prefix">person</i>
              <input
                onChange={(e) => setState({ name: e.target.value })}
                value={state.name}
                type="text"
                id="autocomplete-input"
                class="autocomplete"
              />
              <label for="autocomplete-input">Autocomplete</label>
            </div>
            <div class="input-field col s12">
              <i class="material-icons prefix">email</i>
              <input
                onChange={(e) => setState({ email: e.target.value })}
                value={state.email}
                type="email"
                id="autocomplete-input"
                class="autocomplete"
              />
              <label for="autocomplete-input">Email</label>
            </div>
            <div class="input-field col s12">
              <i class="material-icons prefix">vpn_key</i>
              <input
                onChange={(e) => setState({ password: e.target.value })}
                value={state.password}
                type="password"
                id="autocomplete-input"
                class="autocomplete"
              />
              <label for="autocomplete-input">Password</label>
            </div>
            <button
              class="btn waves-effect waves-light right"
              type="submit"
              name="action"
            >
              Submit
              <i class="material-icons right">send</i>
            </button>
          </form>
        </div>

        <div className="col s6">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Password</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>
            </thead>

            <tbody>
              {state.users.map((user) => (
                <tr key={user.id}>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>{user.password}</td>
                  <td>
                    <button
                      onClick={(e) => onEdit(user.id)}
                      class="btn waves-effect waves-light"
                      type="submit"
                      name="action"
                    >
                      <i class="material-icons">edit</i>
                    </button>
                  </td>
                  <td>
                    <button
                      onClick={(e) => onDelete(user.id)}
                      class="btn waves-effect waves-light"
                      type="submit"
                      name="action"
                    >
                      <i class="material-icons">delete</i>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default App;

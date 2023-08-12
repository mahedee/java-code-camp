function App() {
  const intialState = {
    users: [],
    id: 0,
    name: "",
    email: "",
    password: "",
  };

  return (
    <div className="container">
      {/* <div className="row">
        <div className="col s6">
          <form onSubmit={(e) => this.submit(e, this.state.id)}>
            <div class="input-field col s12">
              <i class="material-icons prefix">person</i>
              <input
                onChange={(e) => this.setState({ name: e.target.value })}
                value={this.state.name}
                type="text"
                id="autocomplete-input"
                class="autocomplete"
              />
              <label for="autocomplete-input">Autocomplete</label>
            </div>
            <div class="input-field col s12">
              <i class="material-icons prefix">email</i>
              <input
                onChange={(e) => this.setState({ email: e.target.value })}
                value={this.state.email}
                type="email"
                id="autocomplete-input"
                class="autocomplete"
              />
              <label for="autocomplete-input">Email</label>
            </div>
            <div class="input-field col s12">
              <i class="material-icons prefix">vpn_key</i>
              <input
                onChange={(e) => this.setState({ password: e.target.value })}
                value={this.state.password}
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
              {this.state.users.map((user) => (
                <tr key={user.id}>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>{user.password}</td>
                  <td>
                    <button
                      onClick={(e) => this.edit(user.id)}
                      class="btn waves-effect waves-light"
                      type="submit"
                      name="action"
                    >
                      <i class="material-icons">edit</i>
                    </button>
                  </td>
                  <td>
                    <button
                      onClick={(e) => this.delete(user.id)}
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
      </div> */}
    </div>
  );
}

export default App;

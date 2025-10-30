import "bootstrap/dist/css/bootstrap.min.css"
import React, { useState } from "react"
import { Container, Row, Col, Card, Form, Button, Alert } from "react-bootstrap"
import { useNavigate } from "react-router-dom"

export default function RegisterPage() {
  const [username, setUsername] = useState("")
  const [nome, setNome] = useState("")
  const [cognome, setCognome] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [msg, setMsg] = useState(null)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMsg(null)
    try {
      const res = await fetch("http://localhost:3001/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username,
          email,
          password,
          nome,
          cognome,
          ruoli: null,
        }),
      })

      if (res.status === 201) {
        setMsg({
          variant: "success",
          text: "Registrazione avvenuta. Effettua il login.",
        })
        setTimeout(() => navigate("/login"), 800)
        return
      }

      const data = await res.json().catch(() => null)
      let text = "Registrazione fallita"
      if (Array.isArray(data)) text = data.join(", ")
      else if (data?.message) text = data.message
      else if (data?.errors) text = JSON.stringify(data.errors)
      setMsg({ variant: "danger", text })
    } catch {
      setMsg({ variant: "danger", text: "Errore di rete" })
    }
  }

  return (
    <Container
      fluid
      className="d-flex align-items-center"
      style={{ paddingTop: 56 }}
    >
      <Row className="w-100 justify-content-center">
        <Col xs={11} sm={10} md={8} lg={5} xl={4}>
          <Card className="shadow-lg rounded border">
            <Card.Body className="p-4">
              <h4 className="mb-3">Register</h4>
              {msg && <Alert variant={msg.variant}>{msg.text}</Alert>}
              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-2" controlId="regUsername">
                  <Form.Label>Username</Form.Label>
                  <Form.Control
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="mariorossi"
                    maxLength={10}
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-2" controlId="regNome">
                  <Form.Label>Nome</Form.Label>
                  <Form.Control
                    value={nome}
                    onChange={(e) => setNome(e.target.value)}
                    placeholder="Mario"
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-2" controlId="regCognome">
                  <Form.Label>Cognome</Form.Label>
                  <Form.Control
                    value={cognome}
                    onChange={(e) => setCognome(e.target.value)}
                    placeholder="Rossi"
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-2" controlId="regEmail">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@example.com"
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-3" controlId="regPassword">
                  <Form.Label>Password</Form.Label>
                  <Form.Control
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••••"
                    required
                  />
                </Form.Group>

                <div className="d-flex justify-content-start">
                  <Button type="submit" variant="primary">
                    Register
                  </Button>
                </div>
              </Form>

              <div className="text-center mt-3">
                <small className="text-muted">
                  Hai già un account?{" "}
                  <span
                    role="button"
                    onClick={() => navigate("/login")}
                    style={{ cursor: "pointer", color: "#0d6efd" }}
                  >
                    Accedi
                  </span>
                </small>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

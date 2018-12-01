package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;

import model.Chamado;
import model.ConnectionFactory;
import model.Funcionario;
import model.Tecnico;
import model.Usuario;

public class ChamadoDao {
	private Connection connection;

	public ChamadoDao() {
		this.connection = new ConnectionFactory().getConnection();
	}

	public void insereChamado(Chamado chamado) {
		String sql = "insert into chamado (funcionario_mat,tipo,departamento,urgencia,titulo,descricao,dtAbertura,status)"
				+ " values (?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement stmt = this.connection.prepareStatement(sql);

			stmt.setLong(1, chamado.getUsuario().getMatricula());
			stmt.setString(2, chamado.getTipo());
			stmt.setString(3, chamado.getDepartamento());
			stmt.setString(4, chamado.getUrgencia());
			stmt.setString(5, chamado.getTitulo());
			stmt.setString(6, chamado.getDescricao());
			stmt.setDate(7, new Date(chamado.getDtAbertura().getTimeInMillis()));
			stmt.setString(8, chamado.getStatus());

			stmt.execute();
			stmt.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Chamado> getListaChamado() {
		try {
			List<Chamado> chamados = new ArrayList<Chamado>();

			PreparedStatement stmt = this.connection.prepareStatement(
					"SELECT funcionario_mat,protocolo,tipo,departamento,urgencia,titulo,descricao,status,dtAbertura FROM chamado");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Chamado chamado = new Chamado();
				Funcionario usuario = new Usuario();
				usuario.setMatricula(rs.getLong("funcionario_mat"));
				chamado.setUsuario(usuario);
				chamado.setProtocolo(rs.getLong("protocolo"));
				chamado.setTipo(rs.getString("tipo"));
				chamado.setDepartamento(rs.getString("departamento"));
				chamado.setUrgencia(rs.getString("urgencia"));
				chamado.setTitulo(rs.getString("titulo"));
				chamado.setDescricao(rs.getString("descricao"));
				chamado.setStatus(rs.getString("status"));

				Calendar dtAbertura = Calendar.getInstance();
				dtAbertura.setTime(rs.getDate("dtAbertura"));
				chamado.setDtAbertura(dtAbertura);

				chamados.add(chamado);
			}
			rs.close();
			stmt.close();
			return chamados;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Chamado> getListaChamado(Funcionario funcionario) {
		// long funcionario_mat = funcionario.getMatricula();
		try {
			List<Chamado> chamados = new ArrayList<Chamado>();

			PreparedStatement stmt = this.connection.prepareStatement(
					"SELECT funcionario_mat,protocolo,tipo,departamento,urgencia,titulo,descricao,status,dtAbertura FROM chamado WHERE funcionario_mat = "
							+ funcionario.getMatricula() + " AND status = 'Pendente' OR status = 'Aberto' OR status = 'Inválido' OR status = 'Em Atendimento'");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Chamado chamado = new Chamado();
				chamado.setProtocolo(rs.getLong("protocolo"));
				chamado.setTipo(rs.getString("tipo"));
				chamado.setDepartamento(rs.getString("departamento"));
				chamado.setUrgencia(rs.getString("urgencia"));
				chamado.setTitulo(rs.getString("titulo"));
				chamado.setDescricao(rs.getString("descricao"));
				chamado.setStatus(rs.getString("status"));

				Calendar dtAbertura = Calendar.getInstance();
				dtAbertura.setTime(rs.getDate("dtAbertura"));
				chamado.setDtAbertura(dtAbertura);

				chamados.add(chamado);
			}
			rs.close();
			stmt.close();
			return chamados;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Chamado> getListaChamadoHistorico(Funcionario funcionario) {
		try {
			List<Chamado> chamados = new ArrayList<Chamado>();

			PreparedStatement stmt = this.connection.prepareStatement("SELECT status,protocolo,departamento,"
					+ "urgencia,titulo,dtAbertura,dtAtendimento,dtConclusao FROM chamado WHERE funcionario_mat = "
					+ funcionario.getMatricula() + " AND status = 'Encerrado' OR status = 'Inválido'");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Chamado chamado = new Chamado();
				chamado.setStatus(rs.getString("status"));
				chamado.setProtocolo(rs.getLong("protocolo"));
				chamado.setDepartamento(rs.getString("departamento"));
				chamado.setUrgencia(rs.getString("urgencia"));
				chamado.setTitulo(rs.getString("titulo"));
				chamado.setStatus(rs.getString("status"));

				Calendar dtAbertura = Calendar.getInstance();
				dtAbertura.setTime(rs.getDate("dtAbertura"));
				chamado.setDtAbertura(dtAbertura);

				Calendar dtAtendimento = Calendar.getInstance();
				dtAtendimento.setTime(rs.getDate("dtAtendimento"));
				chamado.setDtAtendimento(dtAtendimento);

				Calendar dtConclusao = Calendar.getInstance();
				dtConclusao.setTime(rs.getDate("dtConclusao"));
				chamado.setDtConclusao(dtConclusao);

				chamados.add(chamado);
			}
			rs.close();
			stmt.close();
			return chamados;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// Select para Tela Inicial do Técnico
	public List<Chamado> getListaChamadoTelaTec(Funcionario funcionario) {

		Funcionario user;
		Funcionario tec;
		try {
			List<Chamado> chamados = new ArrayList<Chamado>();

			PreparedStatement stmt = this.connection.prepareStatement("SELECT tipo,funcionario_mat,status,protocolo,"
					+ "departamento,urgencia,titulo,dtAbertura,tecnico FROM chamado WHERE status = 'Aberto' OR status = 'Pendente' OR status = 'Em Atendimento'");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				user = new Usuario();
				tec = new Tecnico();
				Chamado chamado = new Chamado();
				chamado.setTipo(rs.getString("tipo"));
				user.setMatricula(rs.getLong("funcionario_mat"));
				chamado.setUsuario(user);
				chamado.setStatus(rs.getString("status"));
				chamado.setProtocolo(rs.getLong("protocolo"));
				chamado.setDepartamento(rs.getString("departamento"));
				chamado.setUrgencia(rs.getString("urgencia"));
				chamado.setTitulo(rs.getString("titulo"));
				chamado.setStatus(rs.getString("status"));

				tec.setMatricula(rs.getLong("tecnico"));
				chamado.setTecnico(tec);

				Calendar dtAbertura = Calendar.getInstance();
				dtAbertura.setTime(rs.getDate("dtAbertura"));
				chamado.setDtAbertura(dtAbertura);

				chamados.add(chamado);
			}
			rs.close();
			stmt.close();
			return chamados;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void atualizaAtender(Funcionario autenticado, Long prot, Chamado chamado) {
		String sql = "UPDATE chamado SET tecnico=?,dtAtendimento=?,status='Em Atendimento' WHERE protocolo=?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, autenticado.getMatricula());
			stmt.setDate(2, new Date(chamado.getDtAtendimento().getTimeInMillis()));
			stmt.setLong(3, prot);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JOptionPane.showMessageDialog(null, "Registro de inicio de Atendimento incluído.");
		}
	}

	public List<Chamado> getListaChamadoHistoricoCompleta(Funcionario funcionario) {
		Funcionario solicitante = new Usuario();
		long matricula;
		try {
			List<Chamado> chamados = new ArrayList<Chamado>();

			PreparedStatement stmt = this.connection.prepareStatement("SELECT status,protocolo,departamento,"
					+ "urgencia,titulo,dtAbertura,dtAtendimento,dtConclusao,funcionario_mat FROM chamado WHERE status = 'Encerrado' OR status = 'Inválido'");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				solicitante.setMatricula(rs.getLong("funcionario_mat"));
				Chamado chamado = new Chamado();
				chamado.setStatus(rs.getString("status"));
				chamado.setProtocolo(rs.getLong("protocolo"));
				chamado.setDepartamento(rs.getString("departamento"));
				chamado.setUrgencia(rs.getString("urgencia"));
				chamado.setTitulo(rs.getString("titulo"));
				chamado.setStatus(rs.getString("status"));
				chamado.setUsuario(solicitante);

				Calendar dtAbertura = Calendar.getInstance();
				dtAbertura.setTime(rs.getDate("dtAbertura"));
				chamado.setDtAbertura(dtAbertura);

				Calendar dtAtendimento = Calendar.getInstance();
				dtAtendimento.setTime(rs.getDate("dtAtendimento"));
				chamado.setDtAtendimento(dtAtendimento);

				Calendar dtConclusao = Calendar.getInstance();
				dtConclusao.setTime(rs.getDate("dtConclusao"));
				chamado.setDtConclusao(dtConclusao);

				chamados.add(chamado);
			}
			rs.close();
			stmt.close();
			return chamados;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Chamado getChamadoProt(Long prot) {
		ChamadoDao dao = new ChamadoDao();
		List<Chamado> chamados = dao.getListaChamado();
		Chamado chamado = new Chamado();
		for (int i = 0; i < chamados.size(); i++) {
			if (chamados.get(i).getProtocolo() == prot) {
				chamado.setProtocolo(chamados.get(i).getProtocolo());
				chamado.setTipo(chamados.get(i).getTipo());
				chamado.setUrgencia(chamados.get(i).getUrgencia());
				chamado.setStatus(chamados.get(i).getStatus());
				chamado.setTitulo(chamados.get(i).getTitulo());
				chamado.setDepartamento(chamados.get(i).getDepartamento());
				chamado.setDescricao(chamados.get(i).getDescricao());
				chamado.setUsuario(chamados.get(i).getUsuario());
			}
		}
		return chamado;
	}
        
        public void atualizaValidar(String verif, Long protoc) {
		String sql = "UPDATE chamado SET status=? WHERE protocolo=?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, verif);
            stmt.setLong(2, protoc);
			stmt.execute();
			stmt.close();
			JOptionPane.showMessageDialog(null, "Alteração de Status registrada.");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
	}
        
        public void atualizaInvalidar(String verif, Long protoc) {
    		String sql = "UPDATE chamado SET status=?, dtAtendimento=?, dtConclusao=? WHERE protocolo=?";
    		Chamado chamado = new Chamado();
    		PreparedStatement stmt = null;
    		try {
    			stmt = connection.prepareStatement(sql);
    			stmt.setString(1, verif);
    			stmt.setDate(2, new Date(chamado.getDtAtendimento().getTimeInMillis()));
    			stmt.setDate(3, new Date(chamado.getDtConclusao().getTimeInMillis()));
                stmt.setLong(4, protoc);
    			stmt.execute();
    			stmt.close();
    			JOptionPane.showMessageDialog(null, "Alteração de Status registrada.");
    		} catch (SQLException e) {
    			throw new RuntimeException(e);
    		} finally {
    			if (stmt != null) {
    				try {
    					stmt.close();
    				} catch (SQLException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}			
    		}
    	}
}

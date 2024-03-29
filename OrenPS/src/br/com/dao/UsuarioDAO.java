package br.com.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;

import br.com.factory.HibernateUtility;
import br.com.models.Usuario;

public class UsuarioDAO extends GenericoDAO<Usuario, Long>{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;/*
	private Connection con;
    private final String COL_ID = "id";
    private final String COL_LOGIN = "login";
    private final String COL_SENHA = "senha";
    private final String COL_ULTIMOACESSO = "ultimo_acesso";*/
/*
    public List<Usuario> listaTodos() throws SQLException {
        List<Usuario> listaUsuario = new ArrayList<Usuario>();
        String query = "select * from usuarios";
        con = ConnectionFactory.getConnection();
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Usuario usuario = new Usuario();
            usuario.setId(rs.getLong(COL_ID));
            usuario.setLogin(rs.getString(COL_LOGIN));
            usuario.setSenha(rs.getString(COL_SENHA));
            usuario.setUltimoAcesso(rs.getTimestamp(COL_ULTIMOACESSO));
            listaUsuario.add(usuario);
        }
        con.close();
        return listaUsuario;
    }

    public Usuario buscaPorId(int id) throws SQLException {
        Usuario usuario = new Usuario();
        String query = "select * from usuarios where id=?";
        con = ConnectionFactory.getConnection();
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            usuario.setId(rs.getInt(COL_ID));
            usuario.setLogin(rs.getString(COL_LOGIN));
            usuario.setSenha(rs.getString(COL_SENHA));
            usuario.setUltimoAcesso(rs.getTimestamp(COL_ULTIMOACESSO));
        }
        con.close();
        return usuario;
    }*/

	public Usuario verificaLoginSenha(Usuario usuario) throws HibernateException, Exception {
		try{
			usuario = (Usuario) HibernateUtility.getSession().createCriteria(Usuario.class)
					.add(Restrictions.eq("login", usuario.getLogin()))
					.add(Restrictions.eq("senha", usuario.getSenha()))
					.uniqueResult();
		}catch(Exception e){
			throw e;
		}
		return usuario;

	}

	public void saveTheme(String theme, Usuario usuario) throws HibernateException, Exception {
		//Nome da classe e atributo
		String updateQuery = "UPDATE Usuario obj SET tema = :valor WHERE obj.id = :idUsuario";  
		HibernateUtility.getSession().createQuery(updateQuery)
		.setString("valor", theme)
		.setLong("idUsuario",usuario.getId())
		.executeUpdate();
		
		HibernateUtility.commitTransaction();
		
	}
	
	public List<Usuario> listaControleEmail()  throws HibernateException, Exception {
		List<Usuario> list = null;
		try{
			Criteria criteriaTable1 = HibernateUtility.getSession().createCriteria(Usuario.class);
			Criteria criteriaTable2 = criteriaTable1.createCriteria("usuarioQuestionario", CriteriaSpecification.LEFT_JOIN);
			//Criteria criteriaTable3 = criteriaTable1.createCriteria("questionario",CriteriaSpecification.LEFT_JOIN);
			
			//criteriaTable1.setFetchMode("usuario", FetchMode.JOIN); 
			
			list = criteriaTable1.list();
			
			//System.out.println(list);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return list;

	}
}

